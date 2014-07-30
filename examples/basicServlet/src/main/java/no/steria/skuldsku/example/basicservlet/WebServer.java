package no.steria.skuldsku.example.basicservlet;

import no.steria.skuldsku.example.basicservlet.recorder.FilterRecorder;
import no.steria.skuldsku.recorder.Recorder;
import no.steria.skuldsku.recorder.recorders.AbstractRecorder;
import no.steria.skuldsku.recorder.recorders.DatabaseRecorder;
import no.steria.skuldsku.recorder.recorders.StreamRecorder;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.handler.ShutdownHandler;
import org.eclipse.jetty.servlet.*;
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
        Recorder.start();
        String warFile = null;
        if (args.length > 0) {
            warFile = args[0];
        }
        new WebServer(getPort(8081),warFile).start();
    }

    private void start() throws Exception {
        Recorder.start();
        Server server = createServer();
        server.start();
        System.out.println(server.getURI());
    }

    private Server createServer() {
        Server server = new Server(port);
        if (warFile != null) {
            WebAppContext webAppContext = new WebAppContext();
            webAppContext.setContextPath("/");
            webAppContext.setWar(warFile);
            server.setHandler(webAppContext);
        } else {
            HandlerList handlerList = new HandlerList();
            handlerList.addHandler(new ShutdownHandler("yablayabla", false, true));
            handlerList.addHandler(new WebAppContext("src/main/webapp", "/"));
            server.setHandler(handlerList);
        }
        return server;
    }

    private static int getPort(int defaultPort) {
        String serverPort = System.getProperty("PORT");
        return serverPort != null && !serverPort.isEmpty() ? Integer.parseInt(serverPort) : defaultPort;
    }

}
