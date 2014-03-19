package no.steria.httpplayer;

import no.steria.httpspy.ReportObject;

import java.io.*;
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
        URLConnection conn = new URL(baseUrl + playStep.getPath()).openConnection();
        conn.setDoOutput(true);
        try (PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(),"utf-8"))) {
            printWriter.append(playStep.getReadInputStream());
        }

        try (InputStream is = conn.getInputStream()) {
            try (Reader reader = new BufferedReader(new InputStreamReader(is, "utf-8"))) {
                //noinspection StatementWithEmptyBody
                while (reader.read() != -1) {}
            }

        }
    }
}
