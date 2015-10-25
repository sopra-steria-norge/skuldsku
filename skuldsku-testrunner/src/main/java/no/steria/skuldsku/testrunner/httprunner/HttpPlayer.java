package no.steria.skuldsku.testrunner.httprunner;

import java.io.BufferedReader;
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
import no.steria.skuldsku.testrunner.httprunner.manipulator.CookieHandler;
import no.steria.skuldsku.testrunner.httprunner.session.CookieSessionIdDecider;
import no.steria.skuldsku.testrunner.httprunner.session.SessionIdDecider;

/**
 * A class for making HTTP-requests using a list of {@link HttpCall}s.
 */
public class HttpPlayer {
    private final String baseUrl;
    
    // TODO: Keep both in one list in order to be able to control execution order.
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

        final RequestData requestData = new RequestData(httpCall.getMethod(),
                httpCall.getPath(),
                httpCall.getHeaders(),
                httpCall.getReadInputStream());
        
        final URL url = new URL(baseUrl + requestData.getRequestPath());
        
        /*
         * TODO: Implement as pure TCP connection in order to have full
         *       control.
         */
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setInstanceFollowRedirects(false);
        conn.setUseCaches(false);
        conn.setAllowUserInteraction(false);
        conn.setRequestMethod(requestData.getRequestMethod());
                
        performRequestManipulation(sessionInstancePlaybackManipulators, requestData);
        
        final boolean doPost = "POST".equals(httpCall.getMethod());        
        writeHeadersToConnection(conn, doPost, requestData.getRequestInput(), requestData.getRequestHeaders());
        writeBodyToConnection(conn, requestData.getRequestInput());


        final int responseStatus = Math.max(0, conn.getResponseCode());
        final Map<String, List<String>> responseHeaders = conn.getHeaderFields();        
        final String response = readResponseFromConnection(conn);
        playStep.setRecorded(response);

        final RequestCompleteData responseData = new RequestCompleteData(
                requestData.getRequestMethod(), requestData.getRequestPath(), requestData.getRequestHeaders(), requestData.getRequestInput(),
                responseStatus, responseHeaders, response);

        reportRequestCompleteData(sessionInstancePlaybackManipulators, responseData);

        if (abortOnFailingRequest) {
            final int resultStatus = conn.getResponseCode();
            if (httpCall.getStatus() != 0
                    && resultStatus != httpCall.getStatus()) {
                throw new FailingRequestException(httpCall.getMethod() + " " + httpCall.getPath() + " expected: " + httpCall.getStatus() + " got: " + resultStatus);
            }
        }
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

    private void performRequestManipulation(final List<PlaybackManipulator> sessionInstancePlaybackManipulators, final RequestData preRequestData) {
        for (PlaybackManipulator manipulator : manipulators) {
            manipulator.performRequestManipulation(preRequestData);
        }
        for (PlaybackManipulator manipulator : sessionInstancePlaybackManipulators) {
            manipulator.performRequestManipulation(preRequestData);
        }
    }
    
    private void reportRequestCompleteData(final List<PlaybackManipulator> sessionInstancePlaybackManipulators, final RequestCompleteData postRequestData) {
        for (PlaybackManipulator manipulator : manipulators) {
            manipulator.reportRequestCompleteData(postRequestData);
        }
        for (PlaybackManipulator manipulator : sessionInstancePlaybackManipulators) {
            manipulator.reportRequestCompleteData(postRequestData);
        }
    }

    private String readResponseFromConnection(final HttpURLConnection conn) throws IOException, UnsupportedEncodingException {
        // TODO: Proper implementation supporting binary data.
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
}
