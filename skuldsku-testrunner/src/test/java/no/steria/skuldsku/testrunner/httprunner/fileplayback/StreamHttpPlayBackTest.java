package no.steria.skuldsku.testrunner.httprunner.fileplayback;

import au.com.bytecode.opencsv.CSVReader;
import no.steria.skuldsku.recorder.httprecorder.HttpCall;
import no.steria.skuldsku.testrunner.httprunner.HttpPlayer;
import no.steria.skuldsku.testrunner.httprunner.PlayStep;
import no.steria.skuldsku.testrunner.httprunner.StreamHttpPlayBack;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static no.steria.skuldsku.testrunner.DbToFileExporter.HTTP_RECORDINGS_HEADER;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class StreamHttpPlayBackTest {

    HttpPlayer httpPlayer = mock(HttpPlayer.class);


    @Test
    public void shouldPlayBackHttpFromStream() throws IOException {
        InputStream inputStream = new ByteArrayInputStream((getHttpRecHeader() + getReportObject1String() + "\n " + getReportObject2String()).getBytes());
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        CSVReader reader = new CSVReader(inputStreamReader);
        StreamHttpPlayBack streamHttpPlayBack = new StreamHttpPlayBack();
        streamHttpPlayBack.play(reader, httpPlayer);

       ArgumentCaptor<PlayStep> playStep = ArgumentCaptor.forClass(PlayStep.class);
        verify(httpPlayer, times(2)).playStep(playStep.capture());
        HttpCall reportObject1 = playStep.getAllValues().get(0).getReportObject();
        HttpCall reportObject2 = playStep.getAllValues().get(1).getReportObject();

        assertEquals("GET", reportObject1.getMethod());
        assertEquals("POST", reportObject2.getMethod());
        assertEquals("/images/bg_exclamation.gif", reportObject1.getPath());
        assertEquals("/css/print.css", reportObject2.getPath());
        assertEquals("IDporten", reportObject1.getHeaders().get("X_SLF_FIRSTNAME").get(0));
        assertEquals("IDpoorten", reportObject2.getHeaders().get("X_SLF_FIRSTNAME").get(0));
    }

    public String getReportObject1String() {
        return "\"qtp2059904228-56\",\"GET\",\"/scripts/wimpel.js\",\"2014-7-25.17.11. 21. 0\",\"1406301123275\",\"" + getData() + "\";";
    }

    private String getData() {
        return "<no.steria.skuldsku.recorder.httprecorder.HttpCall;readInputStream=<null>;parameters=<map>;method=GET;path=/images/bg_exclamation.gif;output=<null>;headers=<map;<java.lang.String;X_SLF_FED>;<java.util.ArrayList;<list;<java.lang.String;true>>>;<java.lang.String;X_SLF_EMAIL>;<java.util.ArrayList;<list;<java.lang.String;idporten@slf.dep.no>>>;<java.lang.String;X_SLF_LASTNAME>;<java.util.ArrayList;<list;<java.lang.String;Bruker>>>;<java.lang.String;X_SLF_IDPORTEN_UID>;<java.util.ArrayList;<list;<java.lang.String;01106000057>>>;<java.lang.String;OSSO-USER-DN>;<java.util.ArrayList;<list;<java.lang.String;cn=idportenfed,cn= External,cn=Users,dc=statenslandbruksforvaltning,dc=no>>>;<java.lang.String;X_SLF_IDPORTEN_SL>;<java.util.ArrayList;<list;<java.lang.String;3>>>;<java.lang.String;X_SLF_PARTICIPANT>;<java.util.ArrayList;<list;<java.lang.String;910228609>>>;<java.lang.String;X_SLF_FIRSTNAME>;<java.util.ArrayList;<list;<java.lang.String;IDporten>>>;<java.lang.String;X_SLF_UID>;<java.util.ArrayList;<list;<java.lang.String;idportenfed>>>;<java.lang.String;X_SLF_ROLES>;<java.util.ArrayList;<list;<java.lang.String;NETTSLFUSER|WESPAFT>>>;<java.lang.String;X-Forwarded-Server>;<java.util.ArrayList;<list;<java.lang.String;test.slf.dep.no>>>;<java.lang.String;X-Forwarded-Proto>;<java.util.ArrayList;<list;<java.lang.String;https>>>;<java.lang.String;X-Forwarded-Host>;<java.util.ArrayList;<list;<java.lang.String;test.slf.dep.no>>>;<java.lang.String;Authorization>;<java.util.ArrayList;<list;<java.lang.String;Basic MDExMDYwMDAwNTc6OTEwMjI4NjA5>>>;<java.lang.String;Cookie>;<java.util.ArrayList;<list;<java.lang.String;JSESSIONID&eq16kprkisf5y23zzi7rswtiukc>>>;<java.lang.String;If-None-Match>;<java.util.ArrayList;<list;<java.lang.String;W/\\\"NHL20IAO3lUNHL3lNj/amg\\\">>>;<java.lang.String;Cache-Control>;<java.util.ArrayList;<list;<java.lang.String;max-age&eq0>>>;<java.lang.String;Accept>;<java.util.ArrayList;<list;<java.lang.String;image/webp,*/*&semiq&eq0.8>>>;<java.lang.String;Connection>;<java.util.ArrayList;<list;<java.lang.String;keep-alive>>>;<java.lang.String;User-Agent>;<java.util.ArrayList;<list;<java.lang.String;Mozilla/5.0 (Windows NT 6.1&semi WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36>>>;<java.lang.String;Referer>;<java.util.ArrayList;<list;<java.lang.String;http://localhost:21110/wimpel/d_tollnedsettelse_resept.jsf>>>;<java.lang.String;If-Modified-Since>;<java.util.ArrayList;<list;<java.lang.String;Mon, 21 Jul 2014 12:42:21 GMT>>>;<java.lang.String;Host>;<java.util.ArrayList;<list;<java.lang.String;localhost:21110>>>;<java.lang.String;Accept-Encoding>;<java.util.ArrayList;<list;<java.lang.String;gzip,deflate,sdch>>>;<java.lang.String;Accept-Language>;<java.util.ArrayList;<list;<java.lang.String;en-US,en&semiq&eq0.8,nb&semiq&eq0.6,nl&semiq&eq0.4>>>>>";
    }

    private String getHttpRecHeader() {
        return "\n" + HTTP_RECORDINGS_HEADER + "\n";
    }

    public String getReportObject2String() {
        return "\"qtp2059904228-52\",\"GET\",\"/images/bg_faux_columns.gif\",\"2014-7-25.17.11. 1. 0\",\"1406301103008\",\"<no.steria.skuldsku.recorder.httprecorder.HttpCall;readInputStream=<null>;parameters=<map>;method=POST;path=/css/print.css;output=<null>;headers=<map;<java.lang.String;X_SLF_FED>;<java.util.ArrayList;<list;<java.lang.String;true>>>;<java.lang.String;X_SLF_EMAIL>;<java.util.ArrayList;<list;<java.lang.String;idporten@slf.dep.no>>>;<java.lang.String;X_SLF_LASTNAME>;<java.util.ArrayList;<list;<java.lang.String;Bruker>>>;<java.lang.String;X_SLF_IDPORTEN_UID>;<java.util.ArrayList;<list;<java.lang.String;01106000057>>>;<java.lang.String;OSSO-USER-DN>;<java.util.ArrayList;<list;<java.lang.String;cn=idportenfed,cn=External,cn=Users,dc=statenslandbruksforvaltning,dc=no>>>;<java.lang.String;X_SLF_IDPORTEN_SL>;<java.util.ArrayList;<list;<java.lang.String;3>>>;<java.lang.String;X_SLF_PARTICIPANT>;<java.util.ArrayList;<list;<java.lang.String;910228609>>>;<java.lang.String;X_SLF_FIRSTNAME>;<java.util.ArrayList;<list;<java.lang.String;IDpoorten>>>;<java.lang.String;X_SLF_UID>;<java.util.ArrayList;<list;<java.lang.String;idportenfed>>>;<java.lang.String;X_SLF_ROLES>;<java.util.ArrayList;<list;<java.lang.String;NETTSLFUSER|WESPAFT>>>;<java.lang.String;X-Forwarded-Server>;<java.util.ArrayList;<list;<java.lang.String;test.slf.dep.no>>>;<java.lang.String;X-Forwarded-Proto>;<java.util.ArrayList;<list;<java.lang.String;https>>>;<java.lang.String;X-Forwarded-Host>;<java.util.ArrayList;<list;<java.lang.String;test.slf.dep.no>>>;<java.lang.String;Authorization>;<java.util.ArrayList;<list;<java.lang.String;Basic MDExMDYwMDAwNTc6OTEwMjI4NjA5>>>;<java.lang.String;Cookie>;<java.util.ArrayList;<list;<java.lang.String;JSESSIONID&eq16kprkisf5y23zzi7rswtiukc>>>;<java.lang.String;If-None-Match>;<java.util.ArrayList;<list;<java.lang.String;W/\"o+KtrNOG67Ao+Ks6It3WUA\">>>;<java.lang.String;Cache-Control>;<java.util.ArrayList;<list;<java.lang.String;max-age&eq0>>>;<java.lang.String;Accept>;<java.util.ArrayList;<list;<java.lang.String;text/css,*/*&semiq&eq0.1>>>;<java.lang.String;Connection>;<java.util.ArrayList;<list;<java.lang.String;keep-alive>>>;<java.lang.String;User-Agent>;<java.util.ArrayList;<list;<java.lang.String;Mozilla/5.0 (Windows NT 6.1&semi WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36>>>;<java.lang.String;Referer>;<java.util.ArrayList;<list;<java.lang.String;http://localhost:21110/wimpel/saksliste.jsf>>>;<java.lang.String;If-Modified-Since>;<java.util.ArrayList;<list;<java.lang.String;Mon, 21 Jul 2014 12:42:21 GMT>>>;<java.lang.String;Host>;<java.util.ArrayList;<list;<java.lang.String;localhost:21110>>>;<java.lang.String;Accept-Encoding>;<java.util.ArrayList;<list;<java.lang.String;gzip,deflate,sdch>>>;<java.lang.String;Accept-Language>;<java.util.ArrayList;<list;<java.lang.String;en-US,en&semiq&eq0.8,nb&semiq&eq0.6,nl&semiq&eq0.4>>>>>\"";
    }
}
