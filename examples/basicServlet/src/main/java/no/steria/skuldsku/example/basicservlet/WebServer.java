package no.steria.skuldsku.example.basicservlet;

import no.steria.skuldsku.recorder.Recorder;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.*;
import org.eclipse.jetty.util.resource.Resource;

import java.io.File;

public class WebServer {
    private final Integer port;

    public WebServer(Integer port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        new WebServer(getPort(8081)).start();
    }

    private void start() throws Exception {
        Recorder.start();
        HandlerList handlerList = new HandlerList();
        ResourceHandler rh = new ResourceHandler();
        if (isDevelopment()) {
            rh.setBaseResource(Resource.newResource(new File("src/main/resources/webapp")));
        }
        else {
            rh.setBaseResource(Resource.newClassPathResource("webapp", true, false));
        }
        rh.setDirectoriesListed(false);
        rh.setWelcomeFiles(new String[]{"/index.html"});
        handlerList.addHandler(rh);
        registerServletsAndFilters(handlerList);
        Server server = new Server(port);
        server.setHandler(handlerList);
        server.start();
        System.out.println(server.getURI());
    }

    private boolean isDevelopment() {
        return new File("pom.xml").exists();
    }


    private void registerServletsAndFilters(HandlerList parent) {
        ServletContextHandler contextHandler = new ServletContextHandler(parent, "/data", true, false);
        ServletHandler handler = contextHandler.getServletHandler();

        handler.addServletWithMapping(PlaceServlet.class,"/place/*");

    }

    private static int getPort(int defaultPort) {
        String port = System.getProperty("PORT");
        Integer serverPort = port != null && !port.isEmpty() ? Integer.parseInt(port) : null;
        return serverPort != null ? serverPort : defaultPort;
    }
}
