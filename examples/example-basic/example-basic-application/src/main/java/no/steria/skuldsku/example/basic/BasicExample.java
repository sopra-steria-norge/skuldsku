package no.steria.skuldsku.example.basic;

import java.io.File;

import no.steria.skuldsku.recorder.Skuldsku;
import no.steria.skuldsku.recorder.SkuldskuConfig;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * The main entry point for the example application.
 */
public class BasicExample {

    public static void main(String[] args) throws Exception {
        setUpSkuldsku();
        startApplication();
    }
    
    
    private static void setUpSkuldsku() {
        Skuldsku.initialize(new SkuldskuConfig("data.txt"));
        Skuldsku.start();
    }

    private static void startApplication() throws Exception {
        final Server server = createServer();
        server.start();
        System.out.println("Started application on: " + server.getURI());
    }

    private static Server createServer() {
        Server server = new Server(getPort());

        WebAppContext webAppContext;
        webAppContext = new WebAppContext();
        webAppContext.setContextPath("/");

        if (isDevEnviroment()) {
            webAppContext.setResourceBase("src/main/resources/webapp");
        } else {
            webAppContext.setBaseResource(Resource.newClassPathResource("webapp", true, false));
        }
        server.setHandler(webAppContext);
        return server;
    }
    
    private static int getPort() {
        final int defaultPort = 8081;
        final String specifiedPort = System.getProperty("PORT");
        return specifiedPort != null && !specifiedPort.isEmpty() ? Integer.parseInt(specifiedPort) : defaultPort;
    }

    private static boolean isDevEnviroment() {
        return new File("pom.xml").exists();
    }
}
