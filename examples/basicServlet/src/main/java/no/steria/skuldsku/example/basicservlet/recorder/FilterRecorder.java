package no.steria.skuldsku.example.basicservlet.recorder;

import no.steria.skuldsku.example.basicservlet.OraclePlaceDao;
import no.steria.skuldsku.example.basicservlet.WebServer;
import no.steria.skuldsku.recorder.httprecorder.CallReporter;
import no.steria.skuldsku.recorder.httprecorder.ServletFilter;
import no.steria.skuldsku.recorder.recorders.AbstractRecorder;
import no.steria.skuldsku.recorder.recorders.DatabaseRecorder;
import no.steria.skuldsku.recorder.recorders.StreamRecorder;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

public class FilterRecorder extends ServletFilter{
    public static final AbstractRecorder recorder = initRecorder();

    private static AbstractRecorder initRecorder() {
        if ("debug".equalsIgnoreCase(System.getProperty("mode"))) {
            System.out.println("DEBUG....");
            return new StreamRecorder(System.out);
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
    }

    @Override
    public void destroy() {

    }
}
