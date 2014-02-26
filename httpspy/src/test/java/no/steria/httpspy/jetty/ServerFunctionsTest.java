package no.steria.httpspy.jetty;

import org.json.JSONObject;
import org.junit.Test;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class ServerFunctionsTest {
    @Test
    public void shouldReturnName() throws Exception {
        JettyServer jettyServer = new JettyServer(0);
        jettyServer.start();

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
            res = toString(is);
        }
        System.out.println(res);

        jettyServer.stop();
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
