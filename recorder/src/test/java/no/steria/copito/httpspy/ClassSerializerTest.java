package no.steria.copito.httpspy;

import no.steria.copito.httpspy.ClassSerializer;
import org.junit.Test;

public class ClassSerializerTest {
    private final String testdata = "<no.steria.copito.httpspy.ReportObject;readInputStream=<null>;parameters=<map;<java.lang.String;oracle.adf.faces.STATE>;&null;<java.lang.String;oracle.adf.faces.STATE_TOKEN>;&null;<java.lang.String;_afPfm>;&null;<java.lang.String;partial>;&null>;method=GET;path=/;output=;headers=<map;<java.lang.String;Accept-Language>;<java.util.ArrayList;<list;<java.lang.String;en-US,en&semiq&eq0.8,da&semiq&eq0.6,nb&semiq&eq0.4>>>;<java.lang.String;Authorization>;<java.util.ArrayList;<list;<java.lang.String;Basic MDExMDYwMDAwNTc6>>>;<java.lang.String;Cookie>;<java.util.ArrayList;<list;<java.lang.String;JSESSIONID&eqm7ofac8b5ztd1dw0p768060pi>>>;<java.lang.String;Host>;<java.util.ArrayList;<list;<java.lang.String;localhost:21110>>>;<java.lang.String;Accept-Encoding>;<java.util.ArrayList;<list;<java.lang.String;gzip,deflate,sdch>>>;<java.lang.String;User-Agent>;<java.util.ArrayList;<list;<java.lang.String;Mozilla/5.0 (Macintosh&semi Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36>>>;<java.lang.String;Connection>;<java.util.ArrayList;<list;<java.lang.String;keep-alive>>>;<java.lang.String;Accept>;<java.util.ArrayList;<list;<java.lang.String;text/html,application/xhtml+xml,application/xml&semiq&eq0.9,image/webp,*/*&semiq&eq0.8>>>;<java.lang.String;Cache-Control>;<java.util.ArrayList;<list;<java.lang.String;max-age&eq0>>>>>";

    @Test
    public void shouldHandleMaps() throws Exception {
        ClassSerializer classSerializer = new ClassSerializer();
        classSerializer.asObject(testdata);

    }
}
