package no.steria.httpspy.jetty;

import no.steria.httpplayer.HttpPlayer;
import no.steria.httpspy.CallReporter;
import no.steria.httpspy.ReportObject;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.util.List;
import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class TestHttpPlayer {
    @Test
    public void shouldHandleBasicForms() throws Exception {
        InMemoryReporter reporter = new InMemoryReporter();
        TestFilter.setReporter(reporter);
        JettyServer jettyServer = new JettyServer(0);
        jettyServer.start();


        try {
            int port = jettyServer.getPort();

            WebDriver browser = new HtmlUnitDriver();
            String baseget = "http://localhost:" + port + "/post/more";
            browser.get(baseget);
            browser.findElement(By.name("firstname")).sendKeys("Darth");
            browser.findElement(By.name("lastname")).sendKeys("Vader");
            browser.findElement(By.name("doPerson")).click();

            assertThat(browser.getPageSource()).contains("Your name is Darth Vader");

            TestFilter.setReporter(null);
            browser.get(baseget);

            String baseurl = "http://localhost:" + port;
            HttpPlayer player = new HttpPlayer(baseurl);
            player.play(reporter.getPlayBook());



        } finally {
            jettyServer.stop();
            TestFilter.setReporter(null);
        }


    }
}
