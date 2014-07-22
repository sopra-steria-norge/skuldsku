package no.steria.skuldsku.example.basicservlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class PlaceServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter writer = resp.getWriter();
        writer
            .append("<html><body>")
            .append("<h2>Add place</h2>")
            .append("<form method='POST' action='addPlace'>")
            .append("<input type='text' name='name'/>")
            .append("<input type='submit' name='addPlace' value='Add'/>")
            .append("</form>")
            .append("</body></html>")
        ;

    }
}
