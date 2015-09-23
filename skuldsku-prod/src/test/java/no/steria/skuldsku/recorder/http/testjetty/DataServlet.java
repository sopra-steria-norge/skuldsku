package no.steria.skuldsku.recorder.http.testjetty;

import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

public class DataServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name;
        try (InputStream inputStream = req.getInputStream()) {
            JSONObject jsonObject;
            jsonObject = new JSONObject(toString(inputStream));
            name = jsonObject.getString("firstname") + " " + jsonObject.getString("lastname");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        resp.setContentType("text/json");
        JSONObject result = new JSONObject();
        try {
            result.put("name",name);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        resp.getWriter().append(result.toString());

    }

    private static String toString(InputStream inputStream) throws IOException {
        // Yes this will close the InputStream twice. Implemented, to see that our logger handles that
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
