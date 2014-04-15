package no.steria.httpplayer.fileplayback;

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
        Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@slfutvdb1.master.no:1521:slfutvdb", "wimpel_dba", "xxx");
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
        httpPlayer.play(script.stream().map(ro -> new PlayStep(ro)).collect(Collectors.toList()));
    }
}