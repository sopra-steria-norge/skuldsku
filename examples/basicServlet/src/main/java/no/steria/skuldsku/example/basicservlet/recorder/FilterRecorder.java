package no.steria.skuldsku.example.basicservlet.recorder;

import no.steria.skuldsku.recorder.httprecorder.CallReporter;
import no.steria.skuldsku.recorder.httprecorder.ServletFilter;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

public class FilterRecorder extends ServletFilter{
    @Override
    public CallReporter getReporter() {
        return new HttpCallReporter();
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("Init");
    }

    @Override
    public void destroy() {

    }
}
