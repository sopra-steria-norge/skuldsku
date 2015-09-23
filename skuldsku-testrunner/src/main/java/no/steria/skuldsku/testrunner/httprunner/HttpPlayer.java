package no.steria.skuldsku.testrunner.httprunner;

import no.steria.skuldsku.recorder.http.HttpCall;
import no.steria.skuldsku.recorder.logging.RecorderLog;
import no.steria.skuldsku.recorder.recorders.FileRecorderReader;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HttpPlayer {
    private final String baseUrl;
    private final List<PlaybackManipulator> manipulators = new ArrayList<>();
    private boolean abortOnFailingRequest = false;

    public HttpPlayer(String baseUrl) {
        this.baseUrl = baseUrl;
        manipulators.add(new CookieHandler());
    }

    
    public void play(String filename) {
        final List<HttpCall> httpCalls = new FileRecorderReader(filename).getRecordedHttp();
        play(httpCalls);
    }

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
    
    public void setAbortOnFailingRequest(boolean abortOnFailingRequest) {
        this.abortOnFailingRequest = abortOnFailingRequest;
    }

    public void playStep(PlayStep playStep) throws IOException {

        HttpCall httpCall = playStep.getReportObject();

        RecorderLog.info(String.format("Step: %s %s ***", httpCall.getMethod(), httpCall.getPath()));

        URL url = new URL(baseUrl + httpCall.getPath());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        String method = httpCall.getMethod();
        conn.setRequestMethod(method);
        String readInputStream = playStep.getReportObject().getReadInputStream();

        for (PlaybackManipulator manipulator : manipulators) {
            readInputStream = manipulator.computePayload(readInputStream);
        }

        Map<String, List<String>> headers = httpCall.getHeaders();

        // adjusts the headers of the request
        for (PlaybackManipulator manipulator : manipulators) {
            headers = manipulator.getHeaders(headers);
        }

        // writing headers
        if (headers != null) {
            Set<Map.Entry<String, List<String>>> entries = headers.entrySet();

            for (Map.Entry<String, List<String>> entry : entries) {
                String key = entry.getKey();
                for (String propval : entry.getValue()) {
                    String val = propval;
                    if ("Content-Length".equals(key) && "POST".equals(method) && readInputStream != null) {
                        val = "" + readInputStream.getBytes().length;
                    }
                    conn.addRequestProperty(key, val);
                }
            }
        }

        // writes the body of the request
        if (readInputStream != null && !readInputStream.isEmpty()) {
            conn.setDoOutput(true);
            try (PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(), "utf-8"))) {
                printWriter.append(readInputStream);
            }
        }
        final Map<String, List<String>> headerFields = conn.getHeaderFields();

        manipulators.forEach(m -> m.reportHeaderFields(headerFields));


        //writes the parameters of the request
        String parameters = httpCall.getParametersRead().entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue()).reduce((a, b) -> a + "&" + b).orElse(null);
        if (parameters != null) {
            conn.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(parameters);
            wr.flush();
            wr.close();
        }

        //recording response from server
        final StringBuilder result = new StringBuilder();
        try (InputStream is = getResponseStream(conn);
             Reader reader = new BufferedReader(new InputStreamReader(is, "utf-8"))) {
                int c;
                while ((c = reader.read()) != -1) {
                    result.append((char) c);
                }
        }

        final String recorded = result.toString();
        playStep.setRecorded(recorded);

        if (abortOnFailingRequest) {
            final int resultStatus = conn.getResponseCode();
            if (httpCall.getStatus() != 0
                    && resultStatus != httpCall.getStatus()) {
                throw new FailingRequestException(httpCall.getMethod() + " " + httpCall.getPath() + " expected: " + httpCall.getStatus() + " got: " + resultStatus);
            }
        }

        manipulators.forEach(m -> m.reportResult(result.toString()));
    }
    
    private InputStream getResponseStream(HttpURLConnection conn) throws IOException {
        return (conn.getResponseCode() >= HttpURLConnection.HTTP_BAD_REQUEST) ? conn.getErrorStream() : conn.getInputStream();
    }
}
