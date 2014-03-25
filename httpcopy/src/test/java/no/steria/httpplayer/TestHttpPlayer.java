package no.steria.httpplayer;

import no.steria.httpspy.CallReporter;
import no.steria.httpspy.ReportObject;
import org.json.JSONObject;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TestHttpPlayer {
    @Test
    public void shouldHandleBasicForms() throws Exception {
        InMemoryReporter reporter = new InMemoryReporter();
        TestFilter.setReporter(reporter);
        JettyServer jettyServer = new JettyServer(0);
        jettyServer.start();


        try {
            int port = jettyServer.getPort();
            JSONObject postObj = new JSONObject();
            postObj.put("firstname","Darth");
            postObj.put("lastname","Vader");

            URLConnection conn = new URL("http://localhost:" + port + "/data").openConnection();
            conn.setDoOutput(true);
            try (PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(),"utf-8"))) {
                printWriter.append(postObj.toString());
            }

            String res;
            try (InputStream is = conn.getInputStream()) {
                res = toString(is);
            }

            CallReporter callReporter = mock(CallReporter.class);
            TestFilter.setReporter(callReporter);


            String baseurl = "http://localhost:" + port;
            HttpPlayer player = new HttpPlayer(baseurl);
            player.play(reporter.getPlayBook());

            ArgumentCaptor<ReportObject> captor = ArgumentCaptor.forClass(ReportObject.class);
            verify(callReporter).reportCall(captor.capture());
            ReportObject reportObject = captor.getValue();

            assertThat(reportObject.getReadInputStream()).isEqualTo(postObj.toString());
            assertThat(reportObject.getOutput()).isEqualTo(res);



        } finally {
            jettyServer.stop();
            TestFilter.setReporter(null);
        }


    }


    private static String toString(InputStream inputStream) throws IOException {
        try (Reader reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"))) {
            StringBuilder result = new StringBuilder();
            int c;
            while ((c = reader.read()) != -1) {
                result.append((char)c);
            }
            return result.toString();
        }
    }
}
