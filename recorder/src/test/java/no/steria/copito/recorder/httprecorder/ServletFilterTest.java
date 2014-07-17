package no.steria.copito.recorder.httprecorder;

import no.steria.copito.recorder.Recorder;
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

import static org.mockito.Mockito.*;

public class ServletFilterTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain chain;

    ServletFilter servletFilter = new TestFilter();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldNotRecordWhenRecordingIsOff() throws IOException, ServletException {
        Recorder.stop();
        servletFilter.doFilter(request, response, chain);
        verifyNoMoreInteractions(request);
        verifyNoMoreInteractions(response);
        verify(chain, times(1)).doFilter(any(HttpServletRequest.class), any(HttpServletResponse.class));
    }
// Behaviour with recording on is covered in ServerFunctionsTest.
}