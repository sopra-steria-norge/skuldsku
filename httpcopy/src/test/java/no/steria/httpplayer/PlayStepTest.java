package no.steria.httpplayer;

import no.steria.httpspy.ReportObject;
import org.fest.assertions.Assertions;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class PlayStepTest {
    @Test
    public void shouldRecord() throws Exception {
        String recordedHtml = "<form method='POST' action='post/something'><input type='text' name='firstname' /><input type='text' name='lastname'/><input type='hidden' name='token' value='secret30574'/><input type='submit' name='doPerson' value='Do it'/></form>";
        ReportObject reportObject = new ReportObject();

        PlayStep recodedStep = new PlayStep(reportObject);

        PlayStep postStep = new PlayStep(null);

        postStep.setReplacement("token",recodedStep);
        recodedStep.record(recordedHtml);

        assertThat(recodedStep.replacement()).isEqualTo("secret30574");

    }
}
