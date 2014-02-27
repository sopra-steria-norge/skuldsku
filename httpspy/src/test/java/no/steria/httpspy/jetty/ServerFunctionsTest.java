package no.steria.httpspy.jetty;

import no.steria.httpspy.CallReporter;
import org.fest.assertions.Assertions;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ServerFunctionsTest {

    @Test
    public void shouldReturnName() throws Exception {
        CallReporter callReporter = mock(CallReporter.class);
        TestFilter.setReporter(callReporter);
        JettyServer jettyServer = new JettyServer(0);
        jettyServer.start();


        try {
            int port = jettyServer.getPort();
            URLConnection conn = new URL("http://localhost:" + port + "/data").openConnection();

            JSONObject postObj = new JSONObject();
            postObj.put("firstname","Darth");
            postObj.put("lastname","Vader");

            conn.setDoOutput(true);
            try (PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(),"utf-8"))) {
                printWriter.append(postObj.toString());
            }

            String res;
            try (InputStream is = conn.getInputStream()) {
                res = toString(conn.getInputStream());
            }
            JSONObject received = new JSONObject(res);
            assertThat(received.getString("name")).isEqualTo("Darth Vader");
            verify(callReporter).reportCall(postObj.toString());
        } finally {
            jettyServer.stop();

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
