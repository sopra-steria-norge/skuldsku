package no.steria.skuldsku.testrunner.httprunner;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import no.steria.skuldsku.recorder.http.HttpCall;
import no.steria.skuldsku.recorder.http.SkuldskuFilter;
import no.steria.skuldsku.recorder.logging.RecorderLog;
import no.steria.skuldsku.recorder.recorders.FileRecorderReader;

/**
 * A class for making HTTP-requests using a list of {@link HttpCall}s.
 */
public class HttpPlayer {
    private final String baseUrl;
    private final List<PlaybackManipulator> manipulators = new ArrayList<>();
    private final List<SessionPlaybackManipulator> sessionManipulators = new ArrayList<>();
    
    private final SessionManager sessionManager = new SessionManager();
    

    private boolean abortOnFailingRequest = false;
    private SessionIdDecider sessionIdDecider = new CookieSessionIdDecider();

    
    /**
     * Initializes this <code>HttpPlayer</code> to be making requests to the
     * given URL.
     *  
     * @param baseUrl The base URL. This should include the context path of
     *          the WAR if {@link SkuldskuFilter} was utilized to make the list of
     *          recorded {@link HttpCall}s.
     */
    public HttpPlayer(String baseUrl) {
        this.baseUrl = removeTrailingSlash(baseUrl);
        sessionManipulators.add(new CookieHandler());
    }

    
    private static String removeTrailingSlash(String s) {
        return s.endsWith("/") ? s.substring(0, s.length() - 1) : s;
    }
    
    /**
     * Plays the HTTP-calls in the given file.
     * @param filename The filename.
     */
    public void play(String filename) {
        final List<HttpCall> httpCalls = new FileRecorderReader(filename).getRecordedHttp();
        play(httpCalls);
    }

    /**
     * Plays the HTTP-calls in the given list.
     * @param httpCalls A list of HTTP calls to be made.
     */
    public void play(List<HttpCall> httpCalls) {
        List<PlayStep> playBook = new ArrayList<>();
        for (HttpCall httpCall : httpCalls) {
            playBook.add(new PlayStep(httpCall));
        }
        playSteps(playBook);
    }
    
    void playSteps(List<PlayStep> playbook) {
        for (PlayStep playStep : playbook) {
            try {
                playStep(playStep);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void addManipulator(PlaybackManipulator manipulator) {
        manipulators.add(manipulator);
    }
    
    public void addManipulator(SessionPlaybackManipulator manipulator) {
        sessionManipulators.add(manipulator);
    }
    
    public void setAbortOnFailingRequest(boolean abortOnFailingRequest) {
        this.abortOnFailingRequest = abortOnFailingRequest;
    }
    
    public void setSessionIdDecider(SessionIdDecider sessionIdDecider) {
        this.sessionIdDecider = sessionIdDecider;
    }

    void playStep(PlayStep playStep) throws IOException {
        final HttpCall httpCall = playStep.getReportObject();
        final String sessionId = sessionIdDecider.determineSessionId(httpCall);
        final List<PlaybackManipulator> sessionInstancePlaybackManipulators = sessionManager.getSessionPlaybackManipulators(sessionId, sessionManipulators);
        
        RecorderLog.info(String.format("Step: %s %s ***", httpCall.getMethod(), httpCall.getPath()));

        final URL url = new URL(baseUrl + httpCall.getPath());
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        
        conn.setRequestMethod(httpCall.getMethod());
        
        String readInputStream = playStep.getReportObject().getReadInputStream();
        readInputStream = performRequestContentManipulation(sessionInstancePlaybackManipulators, readInputStream);

        Map<String, List<String>> headers = httpCall.getHeaders();
        headers = performRequestHeaderManipulation(sessionInstancePlaybackManipulators, headers);
        
        final boolean doPost = "POST".equals(httpCall.getMethod());
        
        writeHeadersToConnection(conn, doPost, readInputStream, headers);
        writeBodyToConnection(conn, readInputStream);

        final Map<String, List<String>> headerFields = conn.getHeaderFields();
        manipulators.forEach(m -> m.reportHeaderFields(headerFields));
        sessionInstancePlaybackManipulators.forEach(m -> m.reportHeaderFields(headerFields));

        //writes the parameters of the request
        // TODO: Check: "parameters" always null?
        /*
        String parameters = httpCall.getParametersRead().entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue()).reduce((a, b) -> a + "&" + b).orElse(null);
        if (parameters != null) {
            conn.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(parameters);
            wr.flush();
            wr.close();
        }
        */

        //recording response from server
        
        final String recorded = readResponseFromConnection(conn);
        playStep.setRecorded(recorded);
        
        if (abortOnFailingRequest) {
            final int resultStatus = conn.getResponseCode();
            if (httpCall.getStatus() != 0
                    && resultStatus != httpCall.getStatus()) {
                throw new FailingRequestException(httpCall.getMethod() + " " + httpCall.getPath() + " expected: " + httpCall.getStatus() + " got: " + resultStatus);
            }
        }

        reportRequestEndedToManipulators(sessionInstancePlaybackManipulators, recorded);
    }

    private void writeBodyToConnection(final HttpURLConnection conn, String readInputStream) throws UnsupportedEncodingException,
            IOException {
        if (readInputStream != null && !readInputStream.isEmpty()) {
            conn.setDoOutput(true);
            try (PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(), "utf-8"))) {
                printWriter.append(readInputStream);
            }
        }
    }


    private void writeHeadersToConnection(final HttpURLConnection conn, final boolean doPost, String readInputStream,
            Map<String, List<String>> headers) {
        if (headers != null) {
            Set<Map.Entry<String, List<String>>> entries = headers.entrySet();

            for (Map.Entry<String, List<String>> entry : entries) {
                String key = entry.getKey();
                for (String propval : entry.getValue()) {
                    String val = propval;
                    if ("Content-Length".equals(key) && doPost && readInputStream != null) {
                        val = "" + readInputStream.getBytes().length;
                    }
                    conn.addRequestProperty(key, val);
                }
            }
        }
    }


    private Map<String, List<String>> performRequestHeaderManipulation(final List<PlaybackManipulator> sessionInstancePlaybackManipulators,
            Map<String, List<String>> headers) {
        for (PlaybackManipulator manipulator : manipulators) {
            headers = manipulator.getHeaders(headers);
        }
        for (PlaybackManipulator manipulator : sessionInstancePlaybackManipulators) {
            headers = manipulator.getHeaders(headers);
        }
        return headers;
    }


    private String performRequestContentManipulation(final List<PlaybackManipulator> sessionInstancePlaybackManipulators, String readInputStream) {
        for (PlaybackManipulator manipulator : manipulators) {
            readInputStream = manipulator.computePayload(readInputStream);
        }
        for (PlaybackManipulator manipulator : sessionInstancePlaybackManipulators) {
            readInputStream = manipulator.computePayload(readInputStream);
        }
        return readInputStream;
    }

    private String readResponseFromConnection(final HttpURLConnection conn) throws IOException, UnsupportedEncodingException {
        final StringBuilder result = new StringBuilder();
        try (InputStream is = getResponseStream(conn);
             Reader reader = new BufferedReader(new InputStreamReader(is, "utf-8"))) {
                int c;
                while ((c = reader.read()) != -1) {
                    result.append((char) c);
                }
        }
        return result.toString();
    }
    
    private InputStream getResponseStream(HttpURLConnection conn) throws IOException {
        return (conn.getResponseCode() >= HttpURLConnection.HTTP_BAD_REQUEST) ? conn.getErrorStream() : conn.getInputStream();
    }
    
    private void reportRequestEndedToManipulators(final List<PlaybackManipulator> sessionInstancePlaybackManipulators, final String recorded) {
        manipulators.forEach(m -> m.reportResult(recorded));
        sessionInstancePlaybackManipulators.forEach(m -> m.reportResult(recorded));
        manipulators.forEach(m -> m.requestEnded());
        sessionInstancePlaybackManipulators.forEach(m -> m.requestEnded());
    }
}
