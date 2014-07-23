package no.steria.skuldsku.example.basicservlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class PlaceServlet extends HttpServlet {
    private PlaceDao placeDao;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter writer = resp.getWriter();
        if ("/add".equals(req.getPathInfo())) {
            writer
                    .append("<html><body>")
                    .append("<h2>Add place</h2>")
                    .append("<form method='POST' action='place/addPlace'>")
                    .append("<input type='text' name='name'/>")
                    .append("<input type='submit' name='addPlace' value='Add'/>")
                    .append("</form>")
                    .append("</body></html>")
            ;
        } else {
            writer
                    .append("<html><body>")
                    .append("<h2>Search</h2>")
                    .append("<form method='GET' action='place/search'>")
                    .append("<input type='text' name='query'/>")
                    .append("<input type='submit' name='search' value='Search'/>")
                    .append("</form>")
                    ;
            writer.append("<ul>");
            List<String> query = placeDao.findMatches(req.getParameter("query"));
            for (String place : query) {
                writer.append(String.format("<li>%s</li>",place));
            }
            writer.append("</body></html>");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        placeDao.addPlace(req.getParameter("name"));
        resp.setContentType("text/html");
        PrintWriter writer = resp.getWriter();
        writer
                .append("<html><body>")
                .append("<p>Place added</p>")
                .append("</body></html>")
        ;
    }

    @Override
    public void init() throws ServletException {
        placeDao = new MemoryPlaceDao();
    }
}
