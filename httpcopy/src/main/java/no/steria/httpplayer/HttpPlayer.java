package no.steria.httpplayer;

import no.steria.httpspy.ReportObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HttpPlayer {
    private String baseUrl;

    public HttpPlayer(String baseUrl) {
        this.baseUrl = baseUrl;
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

    private void doPlayStep(PlayStep playStep) throws IOException {
        ReportObject recordObject = playStep.getReportObject();

        System.out.println(String.format("Step: %s %s ***", recordObject.getMethod(), recordObject.getPath()));

        URL url = new URL(baseUrl + recordObject.getPath());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        String method = recordObject.getMethod();
        conn.setRequestMethod(method);
        if (recordObject.getHeaders() != null) {
            Set<Map.Entry<String, List<String>>> entries = recordObject.getHeaders().entrySet();
            for (Map.Entry<String, List<String>> entry : entries) {
                conn.setRequestProperty(entry.getKey(),entry.getValue().get(0));
            }
        }
        String readInputStream = playStep.inputToSend();
        if (readInputStream != null) {
            conn.setDoOutput(true);
            try (PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(), "utf-8"))) {
                printWriter.append(readInputStream);
            }
        }

        String parameters = recordObject.getParametersRead().entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue()).reduce((a, b) -> a + "&" + b).orElse(null);
        if (parameters != null) {
            conn.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(parameters);
            wr.flush();
            wr.close();
        }

        StringBuilder result = new StringBuilder();
        try (InputStream is = conn.getInputStream()) {
            try (Reader reader = new BufferedReader(new InputStreamReader(is, "utf-8"))) {
                int c;
                while ((c = reader.read()) != -1) {
                    result.append((char)c);
                }
            }

        }

        playStep.record(result.toString());
        System.out.println(result);
    }
}
