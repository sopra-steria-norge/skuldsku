package no.steria.httpplayer;

import no.steria.httpspy.ReportObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class HttpPlayer {
    private String baseUrl;

    public HttpPlayer(String baseUrl) {
        this.baseUrl = baseUrl;
    }


    public void play(List<ReportObject> playbook) {
        for (ReportObject playStep : playbook) {
            try {
                doPlayStep(playStep);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void doPlayStep(ReportObject playStep) throws IOException {

        URL url = new URL(baseUrl + playStep.getPath());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        String method = playStep.getMethod();
        conn.setRequestMethod(method);
        String readInputStream = playStep.getReadInputStream();
        if (readInputStream != null) {
            conn.setDoOutput(true);
            try (PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(), "utf-8"))) {
                printWriter.append(readInputStream);
            }
        }

        String parameters = playStep.getParametersRead().entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue()).reduce((a, b) -> a + "&" + b).orElse(null);
        if (parameters != null) {
            conn.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(parameters);
            wr.flush();
            wr.close();
        }

        try (InputStream is = conn.getInputStream()) {
            try (Reader reader = new BufferedReader(new InputStreamReader(is, "utf-8"))) {
                //noinspection StatementWithEmptyBody
                while (reader.read() != -1) {
                }
            }

        }
    }
}
