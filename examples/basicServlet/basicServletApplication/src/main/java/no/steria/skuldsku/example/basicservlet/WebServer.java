package no.steria.skuldsku.example.basicservlet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ShutdownHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.File;

public class WebServer {
    private final Integer port;
    private String warFile;

    //public static final StreamRecorder recorder = new StreamRecorder(System.out);


    public WebServer(Integer port, String warFile) {
        this.port = port;
        this.warFile = warFile;
    }

    public static void main(String[] args) throws Exception {
        String warFile = null;
        if (args.length > 0) {
            warFile = args[0];
        }
        new WebServer(getPort(8081),warFile).start();
    }

    private void start() throws Exception {
        Server server = createServer();
        server.start();
        System.out.println(server.getURI());
    }

    private Server createServer() {
        Server server = new Server(port);

        WebAppContext webAppContext;
        webAppContext = new WebAppContext();
        // webAppContext.getInitParams().put("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false");
        webAppContext.setContextPath("/");

        if (isDevEnviroment()) {
            webAppContext.setResourceBase("src/main/resources/webapp");
        } else {
            webAppContext.setBaseResource(Resource.newClassPathResource("webapp", true, false));
        }
        server.setHandler(webAppContext);
        return server;
    }

    private boolean isDevEnviroment() {
        return new File("pom.xml").exists();
    }

    private static int getPort(int defaultPort) {
        String serverPort = System.getProperty("PORT");
        return serverPort != null && !serverPort.isEmpty() ? Integer.parseInt(serverPort) : defaultPort;
    }

}
