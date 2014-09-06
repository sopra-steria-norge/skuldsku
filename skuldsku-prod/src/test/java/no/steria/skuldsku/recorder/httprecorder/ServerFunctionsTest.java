package no.steria.skuldsku.recorder.httprecorder;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
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

import no.steria.skuldsku.recorder.Skuldsku;
import no.steria.skuldsku.recorder.SkuldskuAccessor;
import no.steria.skuldsku.recorder.SkuldskuConfig;
import no.steria.skuldsku.recorder.httprecorder.testjetty.JettyServer;
import no.steria.skuldsku.recorder.httprecorder.testjetty.TestFilter;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class ServerFunctionsTest {

    @Before
    public void setUp() throws SQLException {
        SkuldskuAccessor.reset();
        Skuldsku.initialize(new SkuldskuConfig());
        Skuldsku.start();
    }

    @Test
    public void shouldReturnName() throws Exception {
        HttpCallPersister httpCallPersister = mock(HttpCallPersister.class);
        TestFilter.setReporter(httpCallPersister);
        JettyServer jettyServer = new JettyServer(0);
        jettyServer.start();


        try {
            int port = jettyServer.getPort();
            URLConnection conn = new URL("http://localhost:" + port + "/data").openConnection();

            JSONObject postObj = new JSONObject();
            postObj.put("firstname","Darth");
            postObj.put("lastname","Vader");

            conn.setDoOutput(true);
            try (PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(),"utf-8"))) {
                printWriter.append(postObj.toString());
            }

            String res;
            try (InputStream is = conn.getInputStream()) {
                res = toString(is);
            }
            JSONObject received = new JSONObject(res);
            assertThat(received.getString("name")).isEqualTo("Darth Vader");
            ArgumentCaptor<HttpCall> captor = ArgumentCaptor.forClass(HttpCall.class);
            verify(httpCallPersister).reportCall(captor.capture());
            HttpCall httpCall = captor.getValue();

            assertThat(httpCall.getMethod()).isEqualTo("POST");
            assertThat(httpCall.getPath()).isEqualTo("/data");
            assertThat(httpCall.getReadInputStream()).isEqualTo(postObj.toString());
            assertThat(httpCall.getOutput()).isEqualTo(res);

        } finally {
            jettyServer.stop();

        }
    }

    @Test
    public void shouldHandleBasicForms() throws Exception {
        HttpCallPersister httpCallPersister = mock(HttpCallPersister.class);
        TestFilter.setReporter(httpCallPersister);
        JettyServer jettyServer = new JettyServer(0);
        jettyServer.start();


        try {
            int port = jettyServer.getPort();

            WebDriver browser = new HtmlUnitDriver();
            browser.get("http://localhost:" + port + "/post/more");
            browser.findElement(By.name("firstname")).sendKeys("Darth");
            browser.findElement(By.name("lastname")).sendKeys("Vader");
            browser.findElement(By.name("doPerson")).click();

            assertThat(browser.getPageSource()).contains("Your name is Darth Vader");

            ArgumentCaptor<HttpCall> captor = ArgumentCaptor.forClass(HttpCall.class);
            verify(httpCallPersister,times(2)).reportCall(captor.capture());
            List<HttpCall> allValues = captor.getAllValues();

            assertThat(allValues.get(1).getReadInputStream()).isEqualTo("firstname=Darth&lastname=Vader&doPerson=Do+it");

            assertThat(allValues.get(0).getMethod()).isEqualTo("GET");
            assertThat(allValues.get(0).getPath()).isEqualTo("/post/more");
            assertThat(allValues.get(1).getMethod()).isEqualTo("POST");
            assertThat(allValues.get(1).getPath()).isEqualTo("/post/something");
        } finally {
            jettyServer.stop();

        }


    }

    @After
    public void tearDown() throws Exception {
        TestFilter.setReporter(null);
        Skuldsku.stop();
    }

    private static String toString(InputStream inputStream) throws IOException {
        try (Reader reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"))) {
            StringBuilder result = new StringBuilder();
            int c;
            while ((c = reader.read()) != -1) {
                result.append((char)c);
            }
            return result.toString();
        }
    }
}
