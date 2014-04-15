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

        ReportObject postReportObj = new ReportObject();
        postReportObj.setReadInputStream("firstname=Darth&lastname=Vader&token=secret52720&doPerson=Do+it");
        PlayStep postStep = new PlayStep(postReportObj);

        postStep.setReplacement("token",recodedStep);
        recodedStep.record(recordedHtml);

        assertThat(postStep.inputToSend()).isEqualTo("firstname=Darth&lastname=Vader&token=secret30574&doPerson=Do+it");

    }
}
