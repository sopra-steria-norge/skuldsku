package no.steria.httpplayer;

import org.fest.assertions.Assertions;
import org.junit.Test;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.io.*;

public class TestFindValue {
    @Test
    public void shouldFindReplacement() throws Exception {
        String html = toString(getClass().getClassLoader().getResourceAsStream("htmlex.html"));
        PlayStep playStep = new PlayStep(null);
        PlayStep recorded = new PlayStep(null);
        playStep.setReplacement("oracle.adf.faces.STATE_TOKEN", recorded);
        recorded.record(html);
        String replacement = recorded.replacement();
        Assertions.assertThat(replacement).isEqualTo("-zvixy3ypj");
    }

    public static String toString(InputStream inputStream) throws IOException {
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
