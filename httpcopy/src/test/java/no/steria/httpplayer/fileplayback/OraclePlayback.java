package no.steria.httpplayer.fileplayback;

import no.steria.httpplayer.HiddenFieldManipulator;
import no.steria.httpplayer.HttpPlayer;
import no.steria.httpplayer.PlayStep;
import no.steria.httpspy.ReportObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OraclePlayback {
    public static void main(String[] args) throws Exception {
        Class.forName("oracle.jdbc.driver.OracleDriver");
        System.out.println("OK");
        Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@slfutvdb1.master.no:1521:slfutvdb", "wimpel_dba", args[0]);
        List<ReportObject> script = new ArrayList<>();
        try (PreparedStatement stmnt = connection.prepareStatement("select data from trtable order by timest")) {
            ResultSet resultSet = stmnt.executeQuery();
            while (resultSet.next()) {
                String data = resultSet.getString(1);
                ReportObject reportObject = ReportObject.parseFromString(data);
                script.add(reportObject);
            }
        }

        System.out.println("Starting...");

        HttpPlayer httpPlayer = new HttpPlayer("http://localhost:21110/wimpel");
        List<PlayStep> playSteps = script.stream().map(ro -> new PlayStep(ro)).collect(Collectors.toList());
        httpPlayer.addManipulator(new HiddenFieldManipulator("oracle.adf.faces.STATE_TOKEN"));

        /*PlayStep firstGet = playSteps.get(0);
        PlayStep postSakslistge = playSteps.get(2);
        PlayStep postTollnedsettelse = playSteps.get(4);
        postSakslistge.setReplacement("oracle.adf.faces.STATE_TOKEN",firstGet);
        postTollnedsettelse.setReplacement("oracle.adf.faces.STATE_TOKEN",postSakslistge);
        */

        List<PlayStep> playbook = playSteps;

        httpPlayer.play(playbook);
    }
}
