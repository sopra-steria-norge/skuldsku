package no.steria.skuldsku.example.basic;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import no.steria.skuldsku.example.basic.impl.InMemoryPlaceDao;
import no.steria.skuldsku.recorder.Skuldsku;
import no.steria.skuldsku.recorder.java.InstantiationCallback;

public class PlaceServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private PlaceDao placeDao;

    
    @Override
    public void init() throws ServletException {
        placeDao = createPlaceDao();
    }
    
    private PlaceDao createPlaceDao() {
        return Skuldsku.wrap(PlaceDao.class, new InstantiationCallback<PlaceDao>() {
            @Override
            public PlaceDao create() {
                return new InMemoryPlaceDao();
            }
        });
    }
    
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter writer = resp.getWriter();
        if ("/add".equals(req.getPathInfo())) {
            showCreatePage(writer);
        } else {
            showSearchPage(req, writer);
        }
    }

    private void showSearchPage(HttpServletRequest req, PrintWriter writer) {
        writer.append("<html><body>");
        showSearchForm(writer);
        List<String> query = placeDao.findMatches(req.getParameter("query"));
        showSearchResult(writer, query);
        writer.append("</body></html>");
    }

    private void showSearchResult(PrintWriter writer, List<String> query) {
        writer.append("<ul>");
        for (String place : query) {
            writer.append(String.format("<li>%s</li>", place));
        }
        writer.append("</body></html>");
    }

    private void showSearchForm(PrintWriter writer) {
        writer
                .append("<h2>Search</h2>")
                .append("<form method='GET' action='place/search'>")
                .append("<input type='text' name='query'/>")
                .append("<input type='submit' name='search' value='Search'/>")
                .append("</form>")
                ;
    }

    private void showCreatePage(PrintWriter writer) {
        writer
                .append("<html><body>")
                .append("<h2>Add place</h2>")
                .append("<form method='POST' action='place/addPlace'>")
                .append("<input type='text' name='name'/>")
                .append("<input type='submit' name='addPlace' value='Add'/>")
                .append("</form>")
        ;
    }

    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        placeDao.addPlace(req.getParameter("name"));
        showConfirmationPage(resp);
    }

    private void showConfirmationPage(HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html");
        PrintWriter writer = resp.getWriter();
        writer
                .append("<html><body>")
                .append("<p>Place added</p>")
                .append("</body></html>")
        ;
    }
}
