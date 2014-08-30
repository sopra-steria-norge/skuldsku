package no.steria.skuldsku.example.basicservlet.recorder;

import no.steria.skuldsku.example.basicservlet.OraclePlaceDao;
import no.steria.skuldsku.recorder.Skuldsku;
import no.steria.skuldsku.recorder.httprecorder.CallReporter;
import no.steria.skuldsku.recorder.httprecorder.SkuldskuFilter;
import no.steria.skuldsku.recorder.recorders.AbstractRecorderCommunicator;
import no.steria.skuldsku.recorder.recorders.DatabaseRecorderCommunicator;
import no.steria.skuldsku.recorder.recorders.StreamRecorderCommunicator;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class FilterRecorder extends SkuldskuFilter{
    public static final AbstractRecorderCommunicator recorder = initRecorder();

    private static AbstractRecorderCommunicator initRecorder() {
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

    @Override
    public CallReporter getReporter() {
        return recorder;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Skuldsku.start();
    }

    @Override
    public void destroy() {

    }
}
