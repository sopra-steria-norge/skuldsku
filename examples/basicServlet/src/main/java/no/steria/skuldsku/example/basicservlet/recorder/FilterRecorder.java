package no.steria.skuldsku.example.basicservlet.recorder;

import no.steria.skuldsku.example.basicservlet.OraclePlaceDao;
import no.steria.skuldsku.example.basicservlet.WebServer;
import no.steria.skuldsku.recorder.Recorder;
import no.steria.skuldsku.recorder.httprecorder.CallReporter;
import no.steria.skuldsku.recorder.httprecorder.ServletFilter;
import no.steria.skuldsku.recorder.recorders.AbstractRecorder;
import no.steria.skuldsku.recorder.recorders.DatabaseRecorder;
import no.steria.skuldsku.recorder.recorders.StreamRecorder;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import java.io.*;
import java.sql.SQLException;

public class FilterRecorder extends ServletFilter{
    public static final AbstractRecorder recorder = initRecorder();

    private static AbstractRecorder initRecorder() {
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
            return new StreamRecorder(out);
        }
        System.out.println("With DB...");
        return new DatabaseRecorder(OraclePlaceDao.getDataSource());
    }

    @Override
    public CallReporter getReporter() {
        return recorder;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Recorder.start();
    }

    @Override
    public void destroy() {

    }
}
