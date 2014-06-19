package no.steria.copito.recorder.httprecorder;

import no.steria.copito.recorder.RecorderFacade;
import no.steria.copito.recorder.dbrecorder.DatabaseRecorder;
import no.steria.copito.recorder.httprecorder.testjetty.TestFilter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class ServletFilterTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain chain;

    ServletFilter servletFilter = new TestFilter();
    RecorderFacade recorderFacade = new RecorderFacade(new ArrayList<DatabaseRecorder>(0));

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldNotRecordWhenRecordingIsOff() throws IOException, ServletException {
        recorderFacade.stop();
        servletFilter.doFilter(request, response, chain);
        verifyNoMoreInteractions(request);
        verifyNoMoreInteractions(response);
        verify(chain, times(1)).doFilter(any(HttpServletRequest.class), any(HttpServletResponse.class));
    }
// Behaviour with recording on is covered in ServerFunctionsTest.
}