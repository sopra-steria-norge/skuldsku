package no.steria.copito.recorder.httprecorder;

import no.steria.copito.recorder.RecorderFacade;
import no.steria.copito.recorder.httprecorder.testjetty.TestFilter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.Mockito.verifyNoMoreInteractions;

public class ServletFilterTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain chain;

    ServletFilter servletFilter = new TestFilter();
    RecorderFacade recorderFacade = new RecorderFacade(null);

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
        verifyNoMoreInteractions(chain);
    }
// Behaviour with recording on is covered in ServerFunctionsTest.
}