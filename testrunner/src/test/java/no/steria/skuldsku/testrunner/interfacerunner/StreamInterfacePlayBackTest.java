package no.steria.skuldsku.testrunner.interfacerunner;

import no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder.RecordObject;
import no.steria.skuldsku.testrunner.httprunner.HiddenFieldManipulator;
import no.steria.skuldsku.testrunner.httprunner.PlayStep;
import no.steria.skuldsku.testrunner.httprunner.StreamDbPlayBack;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class StreamInterfacePlayBackTest {
    Map<Class, List<RecordObject>> allRecordings = new HashMap<>();

    @Test
    public void shouldRegisterAllMocks() throws IOException, ClassNotFoundException {
        ByteArrayInputStream recordingsSteam = new ByteArrayInputStream(getTestData());
        StreamInterfacePlayBack streamInterfacePlayBack = new StreamInterfacePlayBackTester();
        streamInterfacePlayBack.play(recordingsSteam);
        assertEquals(3, allRecordings.size());
        assertEquals(4, allRecordings.get(StreamDbPlayBack.class).size());
        assertEquals(2, allRecordings.get(HiddenFieldManipulator.class).size());
        assertEquals(1, allRecordings.get(PlayStep.class).size());

        RecordObject recordObject1 = allRecordings.get(StreamDbPlayBack.class).get(2);
        assertEquals("erKonservesvare", recordObject1.getMethod());
        assertEquals("no.steria.skuldsku.testrunner.httprunner.StreamDbPlayBack", recordObject1.getServiceName());

        RecordObject recordObject2 = allRecordings.get(HiddenFieldManipulator.class).get(1);
        assertEquals("arkiverSoknad", recordObject2.getMethod());
        assertEquals("no.steria.skuldsku.testrunner.httprunner.HiddenFieldManipulator", recordObject2.getServiceName());
    }

    private class StreamInterfacePlayBackTester extends StreamInterfacePlayBack {
        @Override
        void registerMock(Class serviceClass, List<RecordObject> recordings) {
            allRecordings.put(serviceClass, recordings);
        }
    }

    private byte[] getTestData() {
        return ("\n" +
                " **JAVA INTERFACE RECORDINGS** \n" +
                "\n" +
                "\"" + StreamDbPlayBack.class.getCanonicalName() + "\",\"erKonservesvare\",\"<java.lang.String;02021000>;<java.lang.String;100>\",\"<java.lang.Boolean;false>\",\"2014-7-25.17.11. 11. 0\",\"pool-2-thread-1\",\"1406301113261\";\n" +
                "\""  + HiddenFieldManipulator.class.getCanonicalName() +  "\",\"hentStatusoppdatering\",\"\",\"<list>\",\"2014-7-25.17.11. 48. 0\",\"pool-2-thread-1\",\"1406301150184\";\n" +
                "\"" + StreamDbPlayBack.class.getCanonicalName() + "\",\"hentLandomraader\",\"\",\"<list;<com.slf.wimpel.dto.Landomraade;<com.slf.wimpel.dto.Landomraade;id=ORD;navn=Alle land;xr=<null>>>;<com.slf.wimpel.dto.Landomraade;<com.slf.wimpel.dto.Landomraade;id=CA;navn=Canada;xr=<null>>>;<com.slf.wimpel.dto.Landomraade;<com.slf.wimpel.dto.Landomraade;id=CL;navn=Chile;xr=<null>>>;<com.slf.wimpel.dto.Landomraade;<com.slf.wimpel.dto.Landomraade;id=PAL;navn=Den palestinske selvstyremyndighet;xr=<null>>>;<com.slf.wimpel.dto.Landomraade;<com.slf.wimpel.dto.Landomraade;id=EFT;navn=EFTA;xr=<null>>>;<com.slf.wimpel.dto.Landomraade;<com.slf.wimpel.dto.Landomraade;id=EU;navn=EU;xr=<null>>>;<com.slf.wimpel.dto.Landomraade;<com.slf.wimpel.dto.Landomraade;id=EG;navn=Egypt;xr=<null>>>;<com.slf.wimpel.dto.Landomraade;<com.slf.wimpel.dto.Landomraade;id=EÃ˜S;navn=EÃ˜S;xr=<null>>>;<com.slf.wimpel.dto.Landomraade;<com.slf.wimpel.dto.Landomraade;id=FO;navn=FÃ¦rÃ¸yene;xr=<null>>>;<com.slf.wimpel.dto.Landomraade;<com.slf.wimpel.dto.Landomraade;id=GLO;navn=Globalt;xr=<null>>>;<com.slf.wimpel.dto.Landomraade;<com.slf.wimpel.dto.Landomraade;id=GL;navn=GrÃ¸nland;xr=<null>>>;<com.slf.wimpel.dto.Landomraade;<com.slf.wimpel.dto.Landomraade;id=IL;navn=Israel;xr=<null>>>;<com.slf.wimpel.dto.Landomraade;<com.slf.wimpel.dto.Landomraade;id=JO;navn=Jordan;xr=<null>>>;<com.slf.wimpel.dto.Landomraade;<com.slf.wimpel.dto.Landomraade;id=LM;navn=Lavere mellominntektsland;xr=<null>>>;<com.slf.wimpel.dto.Landomraade;<com.slf.wimpel.dto.Landomraade;id=LB;navn=Libanon;xr=<null>>>;<com.slf.wimpel.dto.Landomraade;<com.slf.wimpel.dto.Landomraade;id=MK;navn=Makedonia;xr=<null>>>;<com.slf.wimpel.dto.Landomraade;<com.slf.wimpel.dto.Landomraade;id=MA;navn=Marokko;xr=<null>>>;<com.slf.wimpel.dto.Landomraade;<com.slf.wimpel.dto.Landomraade;id=MX;navn=Mexico;xr=<null>>>;<com.slf.wimpel.dto.Landomraade;<com.slf.wimpel.dto.Landomraade;id=MUL;navn=Minst utviklede land;xr=<null>>>;<com.slf.wimpel.dto.Landomraade;<com.slf.wimpel.dto.Landomraade;id=PE;navn=Peru;xr=<null>>>;<com.slf.wimpel.dto.Landomraade;<com.slf.wimpel.dto.Landomraade;id=SG;navn=Singapore;xr=<null>>>;<com.slf.wimpel.dto.Landomraade;<com.slf.wimpel.dto.Landomraade;id=KR;navn=SÃ¸r Korea;xr=<null>>>;<com.slf.wimpel.dto.Landomraade;<com.slf.wimpel.dto.Landomraade;id=TN;navn=Tunisia;xr=<null>>>;<com.slf.wimpel.dto.Landomraade;<com.slf.wimpel.dto.Landomraade;id=TR;navn=Tyrkia;xr=<null>>>;<com.slf.wimpel.dto.Landomraade;<com.slf.wimpel.dto.Landomraade;id=US;navn=USA;xr=<null>>>;<com.slf.wimpel.dto.Landomraade;<com.slf.wimpel.dto.Landomraade;id=UA;navn=Ukraina;xr=<null>>>;<com.slf.wimpel.dto.Landomraade;<com.slf.wimpel.dto.Landomraade;id=GSP;navn=Utviklingsland;xr=<null>>>>\",\"2014-7-25.17.11. 7. 0\",\"pool-2-thread-1\",\"1406301109661\";\n" +
                "\"" + StreamDbPlayBack.class.getCanonicalName() + "\",\"erKonservesvare\",\"<java.lang.String;02021000>;<java.lang.String;100>\",\"<java.lang.Boolean;false>\",\"2014-7-25.17.11. 9. 0\",\"pool-2-thread-1\",\"1406301111558\";\n" +
                "\"" + HiddenFieldManipulator.class.getCanonicalName() + "\",\"arkiverSoknad\",\"<com.slf.wimpel.database.hibernate.model.Soeknad;serialVersionUID=7444915242060253866;soekSaksnrWimpel=56424;TStatusType=<com.slf.wimpel.database.hibernate.model.StatusType;statKode=UU;statNavn=Under utfylling;statBeskrivelse=X;statAktiv=J;Soeknads=<org.hibernate.collection.PersistentSet;set=<null>;tempList=<null>>;THendelses=<org.hibernate.collection.PersistentSet;set=<null>;tempList=<null>>>;TReseptTnt=<com.slf.wimpel.database.hibernate.model.ReseptTNT;restId=40823;restOrgnr=910228609;restTollvareNr=02021000;restTollvareNavn=Storfe fryst, hele/halve skrotter;restVarenr=100;restVareNavn=Storfe, fryst, hele/halve skrotter;restFakturabetegnelse=Rundervlees;restAktiv=J;restOpprettetAv=01106000057;restOpprettetDato=20140725171154031;restEndretAv=01106000057;restEndretDato=20140725171206797;TRestRaavares=<org.hibernate.collection.PersistentSet;set=[];tempList=<null>>;Soeknads=<org.hibernate.collection.PersistentSet;set=[];tempList=<null>>>;TReseptRaak=<null>;SoeknadType=<com.slf.wimpel.database.hibernate.model.SoeknadType;soektId=3;soektKortnavn=Tollnedsettelse;soektNavn=Tollnedsettelse ved import av landbruksvarer;soektBeskrivelse=<null>;soektAktiv=J;Soeknads=<org.hibernate.collection.PersistentSet;set=<null>;tempList=<null>>>;TRaakSoeknadType=<null>;soekSaksnr=2014004027;nyttSoekSaksnr=<null>;soekOrgnr=910228609;soekOrgNavn=NORREK DYPFRYS AS;soekOrgAdresse1=Helgeroveien 892;soekOrgAdresse2=<null>;soekOrgAdresse3=<null>;soekOrgPostnr=3267;soekOrgPoststed=LARVIK;soekOrgLand=Norge;soekOrgTelefon=33 16 54 00;soekOrgTelefax=33 16 54 01;soekOrgEpost=MAIL@NORREK.NO;soekKontaktperson=Kathinka Lone;soekVarslingEpost1=arbresha.shamolli@slf.dep.no;soekVarslingEpost2=<null>;soekVarslingEpost3=<null>;soekElektroniskVedtak=J;soekOpprettetAv=01106000057;soekOpprettetDato=20140725171146199;soekEndretAv=01106000057;soekEndretDato=20140725171206797;soekSendtDato=20140725171206796;soekVedtakGyldigTil=<null>;soekDokIhhtKrav=J;soekEgenerklaering=N;soekSoeknadAar=<null>;soekPeriodeFra=<null>;soekPeriodeTil=<null>;soekTollvareNr=02021000;soekTollvareNavn=Storfe fryst, hele/halve skrotter;soekTvareUnntakRaak=N;soekVareNr=100;soekVareNavn=Storfe, fryst, hele/halve skrotter;soekVareSammensatt=J;soekVareUnntakRaak=N;soekLandId=CA;soekLandNavn=Canada;soekFakturabetegnelse=Rundervlees;soekOekologisk=N;soekKonserves=N;soekKjoeperOrgnr=<null>;soekKjoperNavn=<null>;soekKvoteId=<null>;soekKvoteandelId=<null>;soekKvoteNavn=<null>;soekMengde=<null>;soekMengdeBenevnelse=<null>;soekTilleggstekst=<null>;soekVilkaar=<null>;vilkaarnr=<null>;paragraf=<null>;upkt=<null>;THendelses=<org.hibernate.collection.PersistentSet;set=[];tempList=<null>>;linjer=<org.hibernate.collection.PersistentSet;set=[];tempList=<null>>;bearbeidingsprosess=<null>;sumSolgteVarer=<null>;anvendelse=<null>;annet=<null>>;<com.slf.wimpel.arkiv.FokusSoeknad;TNT_RESEPT>;<java.lang.String;Tollnedsettelse NORREK DYPFRYS AS>;<java.lang.String;Rundervlees>;<java.lang.String;SÃ¸knad tollnedsettelse>;<java.lang.String;Rundervlees>;<null>\",\"<com.slf.wimpel.arkiv.ArkivReferanse;arkivsakId=2014004027;journalpostId=2014005023>\",\"2014-7-25.17.11. 59. 0\",\"pool-2-thread-1\",\"1406301161632\";\n" +
                "\""  + PlayStep.class.getCanonicalName() + "\",\"hentNyeVedtak\",\"\",\"<list>\",\"2014-7-25.17.11. 48. 0\",\"pool-2-thread-1\",\"1406301150181\";\n" +
                "\"" + StreamDbPlayBack.class.getCanonicalName() + "\",\"erKonservesvare\",\"<java.lang.String;02021000>;<java.lang.String;100>\",\"<java.lang.Boolean;false>\",\"2014-7-25.17.11. 9. 0\",\"pool-2-thread-1\",\"1406301111505\";\n" +
                "\n" +
                "\n" +
                " **HTTP RECORDINGS** \n").getBytes();
    }


}