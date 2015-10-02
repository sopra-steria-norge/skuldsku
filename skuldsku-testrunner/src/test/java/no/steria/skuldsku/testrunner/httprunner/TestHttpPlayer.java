package no.steria.skuldsku.testrunner.httprunner;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import no.steria.skuldsku.recorder.Skuldsku;
import no.steria.skuldsku.recorder.SkuldskuAccessor;
import no.steria.skuldsku.recorder.SkuldskuConfig;
import no.steria.skuldsku.recorder.http.HttpCall;
import no.steria.skuldsku.recorder.http.HttpCallPersister;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class TestHttpPlayer {

    @Before
    public void setUp() throws SQLException {
        SkuldskuAccessor.reset();
        Skuldsku.initialize(new SkuldskuConfig());
        Skuldsku.start();
    }

    @Test
    public void shouldHandleJSONPost() throws Exception {
        InMemoryReporter reporter = new InMemoryReporter();
        TestFilter.setReporter(reporter);
        JettyServer jettyServer = new JettyServer(0);
        jettyServer.start();


        try {
            int port = jettyServer.getPort();
            JSONObject postObj = new JSONObject();
            postObj.put("firstname", "Darth");
            postObj.put("lastname", "Vader");

            URLConnection conn = new URL("http://localhost:" + port + "/data").openConnection();
            conn.setDoOutput(true);
            try (PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(), "utf-8"))) {
                printWriter.append(postObj.toString());
            }

            String res;
            try (InputStream is = conn.getInputStream()) {
                res = toString(is);
            }

            HttpCallPersister callReporter = mock(HttpCallPersister.class);
            TestFilter.setReporter(callReporter);


            String baseurl = "http://localhost:" + port;
            HttpPlayer player = new HttpPlayer(baseurl);
            player.play(reporter.getPlayBook());

            ArgumentCaptor<HttpCall> captor = ArgumentCaptor.forClass(HttpCall.class);
            verify(callReporter).reportCall(captor.capture());
            HttpCall reportObject = captor.getValue();

            assertThat(reportObject.getReadInputStream()).isEqualTo(postObj.toString());
            assertThat(reportObject.getOutput()).isEqualTo(res);

        } finally {
            jettyServer.stop();
            TestFilter.setReporter(null);
        }


    }

    @Test
    public void shouldHandleBasicForms() throws Exception {
        InMemoryReporter reporter = new InMemoryReporter();
        TestFilter.setReporter(reporter);

        JettyServer jettyServer = new JettyServer(0);
        jettyServer.start();


        try {
            int port = jettyServer.getPort();

            final String baseUrl = "http://localhost:" + port;
            WebDriver browser = new HtmlUnitDriver();
            browser.get(baseUrl + "/post/more");
            browser.findElement(By.name("firstname")).sendKeys("Darth");
            browser.findElement(By.name("lastname")).sendKeys("Vader");
            browser.findElement(By.name("doPerson")).submit();
            
            final List<HttpCall> httpCalls = reporter.getPlayBook();
            assertThat(httpCalls).hasSize(2); // GET+POST
            List<PlayStep> playbook = httpCalls.stream().map(PlayStep::new).collect(Collectors.toList());

            
            final HttpCallPersister callReporter = mock(HttpCallPersister.class);
            TestFilter.setReporter(callReporter);
            
            final HttpPlayer player = new HttpPlayer(baseUrl);
            player.addManipulator(new HiddenFieldManipulator("token"));
            player.playSteps(playbook);

            assertThat(playbook.get(1).getRecorded()).contains("Your name is Darth Vader");
        } finally {
            jettyServer.stop();
        }


    }


    private static String toString(InputStream inputStream) throws IOException {
        try (Reader reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"))) {
            StringBuilder result = new StringBuilder();
            int c;
            while ((c = reader.read()) != -1) {
                result.append((char) c);
            }
            return result.toString();
        }
    }
}
