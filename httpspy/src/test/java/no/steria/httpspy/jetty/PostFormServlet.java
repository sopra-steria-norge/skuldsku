package no.steria.httpspy.jetty;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class PostFormServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();
        resp.setContentType("text/html");
        writer.append("<form method='POST' action='post'>");
        writer.append("<input type='text' name='firstname' />");
        writer.append("<input type='text' name='lastname'/>");
        writer.append("<input type='submit' name='doPerson' value='Do it'/>");
        writer.append("</form>");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String firstname = req.getParameter("firstname");
        String lastname = req.getParameter("lastname");

        String name = firstname + " " + lastname;

        resp.setContentType("text/html");

        resp.getWriter().append("Your name is " + name);

    }
}
