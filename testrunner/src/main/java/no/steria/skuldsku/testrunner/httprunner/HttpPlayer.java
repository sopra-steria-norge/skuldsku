package no.steria.skuldsku.testrunner.httprunner;

import no.steria.skuldsku.recorder.httprecorder.ReportObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class HttpPlayer {
    private String baseUrl;
    private List<PlaybackManipulator> manipulators = new ArrayList<>();

    public HttpPlayer(String baseUrl) {
        this.baseUrl = baseUrl;
        manipulators.add(new CookieHandler());
    }


    public void play(List<PlayStep> playbook) {
        for (PlayStep playStep : playbook) {
            try {
                doPlayStep(playStep);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void addManipulator(PlaybackManipulator manipulator) {
        manipulators.add(manipulator);
    }

    private <S> S doAllmanipulatorsx(Stream<Function<S,S>> doIt, S start) {
        Optional<Function<S, S>> reduce = doIt.reduce((a, b) -> a.andThen(b));
        if (!reduce.isPresent()) {
            return start;
        }
        S result = reduce.get().apply(start);
        return result;
    }






    private void doPlayStep(PlayStep playStep) throws IOException {

        ReportObject recordObject = playStep.getReportObject();

        System.out.println(String.format("Step: %s %s ***", recordObject.getMethod(), recordObject.getPath()));

        URL url = new URL(baseUrl + recordObject.getPath());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        String method = recordObject.getMethod();
        conn.setRequestMethod(method);

        //String readInputStream = playStep.inputToSend();
        String readInputStream = playStep.getReportObject().getReadInputStream();

        for (PlaybackManipulator manipulator : manipulators) {
            readInputStream = manipulator.computePayload(readInputStream);
        }

        System.out.println(readInputStream);
        Map<String, List<String>> headers = recordObject.getHeaders();

        for (PlaybackManipulator manipulator : manipulators) {
            headers = manipulator.getHeaders(headers);
        }

        if (headers != null) {
            Set<Map.Entry<String, List<String>>> entries = headers.entrySet();

            for (Map.Entry<String, List<String>> entry : entries) {
                String key = entry.getKey();
                for (String propval : entry.getValue()) {
                    String val = propval;
                    if ("Content-Length".equals(key) && "POST".equals(method) && readInputStream != null) {
                        val = "" + readInputStream.length();
                    }
                    conn.addRequestProperty(key, val);
                }
            }
        }
        if (readInputStream != null && !readInputStream.isEmpty()) {
            conn.setDoOutput(true);
            try (PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(), "utf-8"))) {
                printWriter.append(readInputStream);
            }
        }
        final Map<String, List<String>> headerFields = conn.getHeaderFields();

        manipulators.forEach(m -> m.reportHeaderFields(headerFields));


        String parameters = recordObject.getParametersRead().entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue()).reduce((a, b) -> a + "&" + b).orElse(null);
        if (parameters != null) {
            conn.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(parameters);
            wr.flush();
            wr.close();
        }



        final StringBuilder result = new StringBuilder();
        try (InputStream is = conn.getInputStream()) {
            try (Reader reader = new BufferedReader(new InputStreamReader(is, "utf-8"))) {
                int c;
                while ((c = reader.read()) != -1) {
                    result.append((char)c);
                }
            }

        }

        playStep.record(result.toString());

        manipulators.forEach(m -> m.reportResult(result.toString()));
        System.out.println(result);
    }
}
