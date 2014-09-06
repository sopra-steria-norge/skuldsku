package no.steria.skuldsku.example.basicservlet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import no.steria.skuldsku.recorder.Skuldsku;
import no.steria.skuldsku.recorder.SkuldskuConfig;
import no.steria.skuldsku.recorder.recorders.AbstractRecorderCommunicator;
import no.steria.skuldsku.recorder.recorders.DatabaseRecorderCommunicator;
import no.steria.skuldsku.recorder.recorders.StreamRecorderCommunicator;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;

public class WebServer {
    private final Integer port;
    private String warFile;

    //public static final StreamRecorder recorder = new StreamRecorder(System.out);


    public WebServer(Integer port, String warFile) {
        this.port = port;
        this.warFile = warFile;
    }

    public static void main(String[] args) throws Exception {
        setUpSkuldsku();
        
        String warFile = null;
        if (args.length > 0) {
            warFile = args[0];
        }
        new WebServer(getPort(8081),warFile).start();
    }
    
    private static void setUpSkuldsku() {
        final SkuldskuConfig config = new SkuldskuConfig();
        AbstractRecorderCommunicator c = createRecorderCommunicator();
        config.setJavaIntefaceCallPersister(c);
        config.setHttpCallPersister(c);
        Skuldsku.initialize(config);
        Skuldsku.start();
    }
    
    private static AbstractRecorderCommunicator createRecorderCommunicator() {
        if ("debug".equalsIgnoreCase(System.getProperty("mode"))) {
            System.out.println("DEBUG....");
            OutputStream out = System.out;
            String outfile = System.getProperty("outfile");
            if (outfile != null && !outfile.isEmpty()) {
                System.out.println("Writing to file " + outfile);
                try {
                    out = new FileOutputStream(new File(outfile));
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
            return new StreamRecorderCommunicator(out);
        }
        System.out.println("With DB...");
        return new DatabaseRecorderCommunicator(OraclePlaceDao.getDataSource());
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
