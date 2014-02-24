package no.steria.httpspy.jetty;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ShutdownHandler;
import org.eclipse.jetty.webapp.WebAppContext;

public class JettyServer {

    private final Integer port;

    public JettyServer(Integer port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        new JettyServer(getPort(8081)).start();
    }

    private void start() throws Exception {
        Server server = new Server(port);
        HandlerList handlerList = new HandlerList();
        handlerList.addHandler(new ShutdownHandler("yablayaxxx", false, true));
        handlerList.addHandler(new WebAppContext("src/test/webapp", "/"));
        server.setHandler(handlerList);
        server.start();
        System.out.println(server.getURI());
    }

    private static Integer getPort(int defaultPort) {
        String envPort = System.getenv("PORT");
        return Integer.valueOf(envPort == null ? String.valueOf(defaultPort) : envPort);
    }
}