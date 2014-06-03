package no.steria.copito.httpplayer;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class PostFormServlet extends HttpServlet {
    private Random random = new Random();
    private static String myToken;
    private static String cookieValue;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String token = "secret" + random.nextInt(100000);
        myToken = token;
        PrintWriter writer = resp.getWriter();
        resp.setContentType("text/html");
        writer.append("<form method='POST' action='post/something'>");
        writer.append("<input type='text' name='firstname' />");
        writer.append("<input type='text' name='lastname'/>");
        writer.append("<input type='hidden' name='token' value='" + token + "'/>");
        writer.append("<input type='submit' name='doPerson' value='Do it'/>");
        writer.append("</form>");
        cookieValue = "cookie" + random.nextInt(100000);
        Cookie userCookie = new Cookie("myCookie",cookieValue);
        userCookie.setMaxAge(120);
        resp.addCookie(userCookie);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter writer = resp.getWriter();

        String paratoken = req.getParameter("token");
        if (!myToken.equals(paratoken)) {
            writer.append("Sorry your token is wrong");
            writer.close();
            return;
        }

        boolean foundCookie = false;

        Cookie[] cookies = req.getCookies();
        for (Cookie cookie : cookies) {
            if ("myCookie".equals(cookie.getName()) && cookieValue.equals(cookie.getValue())) {
                foundCookie = true;
                break;
            }
        }
        if (!foundCookie) {
            writer.append("Sorry your cookie is wrong");
            writer.close();
            return;
        }


        String firstname = req.getParameter("firstname");
        String lastname = req.getParameter("lastname");

        String name = firstname + " " + lastname;

        writer.append("Your name is " + name);
        writer.close();

    }
}
