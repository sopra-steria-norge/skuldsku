package no.steria.skuldsku.recorder.http;

import no.steria.skuldsku.recorder.Skuldsku;
import no.steria.skuldsku.recorder.SkuldskuAccessor;
import no.steria.skuldsku.recorder.SkuldskuConfig;
import no.steria.skuldsku.recorder.http.SkuldskuFilter;
import no.steria.skuldsku.recorder.http.testjetty.TestFilter;

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

public class SkuldskuFilterTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain chain;

    SkuldskuFilter skuldskuFilter = new TestFilter();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() {
        SkuldskuAccessor.reset();
        Skuldsku.initialize(new SkuldskuConfig());
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldNotRecordWhenRecordingIsOff() throws IOException, ServletException {
        Skuldsku.stop();
        skuldskuFilter.doFilter(request, response, chain);
        verifyNoMoreInteractions(request);
        verifyNoMoreInteractions(response);
        verify(chain, times(1)).doFilter(any(HttpServletRequest.class), any(HttpServletResponse.class));
    }
// Behaviour with recording on is covered in ServerFunctionsTest.
}