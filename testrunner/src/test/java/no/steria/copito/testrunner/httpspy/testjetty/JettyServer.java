package no.steria.copito.testrunner.httpspy.testjetty;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ShutdownHandler;
import org.eclipse.jetty.webapp.WebAppContext;

public class JettyServer {

    private final Integer port;
    private Server server;

    public JettyServer(Integer port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        new JettyServer(getPort(8081)).start();
    }

    public void start() throws Exception {
        server = new Server(port);
        HandlerList handlerList = new HandlerList();
        handlerList.addHandler(new ShutdownHandler("yablayaxxx", false, true));
        handlerList.addHandler(new WebAppContext("src/test/webapp", "/"));
        server.setHandler(handlerList);
        server.start();
        System.out.println(server.getURI());
    }

    public int getPort() {
        return ((ServerConnector) server.getConnectors()[0]).getLocalPort();
    }

    public void stop() throws Exception {
        server.stop();
    }

    private static Integer getPort(int defaultPort) {
        String envPort = System.getenv("PORT");
        return Integer.valueOf(envPort == null ? String.valueOf(defaultPort) : envPort);
    }
}