package no.steria.copito.testrunner.httprunner.fileplayback;

import no.steria.copito.recorder.httprecorder.ReportObject;
import no.steria.copito.testrunner.httprunner.HiddenFieldManipulator;
import no.steria.copito.testrunner.httprunner.HttpPlayer;
import no.steria.copito.testrunner.httprunner.PlayStep;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static no.steria.copito.recorder.Recorder.COPITO_DATABASE_TABLE_PREFIX;

public class OraclePlayback {
    private static class Played {

        private final String service;
        private final String method;
        private final String parameters;
        private final String result;

        public Played(String service, String method, String parameters, String result) {
            this.service = service;
            this.method = method;
            this.parameters = parameters;
            this.result = result;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Played played = (Played) o;

            if (method != null ? !method.equals(played.method) : played.method != null) return false;
            if (parameters != null ? !parameters.equals(played.parameters) : played.parameters != null) return false;
            if (result != null ? !result.equals(played.result) : played.result != null) return false;
            if (service != null ? !service.equals(played.service) : played.service != null) return false;

            return true;
        }

        public String reportDiff(Played played) {
            if (equals(played)) {
                return null;
            }
            List<String> diff = new ArrayList<>();
            if (!nullSafeEquals(method,played.method)) {
                diff.add("method");
            }
            if (!nullSafeEquals(parameters,played.parameters)) {
                diff.add("parameters");
            }if (!nullSafeEquals(result,played.result)) {
                diff.add("result");
            }if (!nullSafeEquals(service,played.service)) {
                diff.add("service");
            }
            return diff.stream().reduce((a,b) -> a + "," + b).get();
        }

        private <T> boolean nullSafeEquals(T a, T b) {
            return a != null ? a.equals(b) : b == null;
        }



        @Override
        public int hashCode() {
            int result1 = service != null ? service.hashCode() : 0;
            result1 = 31 * result1 + (method != null ? method.hashCode() : 0);
            result1 = 31 * result1 + (parameters != null ? parameters.hashCode() : 0);
            result1 = 31 * result1 + (result != null ? result.hashCode() : 0);
            return result1;
        }

        public String getService() {
            return service;
        }

        public String getMethod() {
            return method;
        }

        public String getParameters() {
            return parameters;
        }

        public String getResult() {
            return result;
        }


    }



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


        deleteJavaLogg(connection);
        runit(script);

        List<Played> recordOne = getPlayed(connection);

        deleteJavaLogg(connection);
        runit(script);
        List<Played> recordTwo = getPlayed(connection);

        recordOne = filterUnwanted(recordOne);
        recordTwo = filterUnwanted(recordTwo);
        compare(recordOne, recordTwo);
    }


    private static void matchAndReport(List<Played> recordOne,List<Played> recordTwo,BiFunction<Played,Played,String> analyze) {
        for (int i=0;i<recordOne.size();i++) {
            Played one = recordOne.get(i);
            Played two = recordTwo.get(i);
            String res = analyze.apply(one,two);
            if (res != null) {
                System.out.println(i + ". " + res);
            }
        }
    }

    private static void compare(List<Played> recordOne, List<Played> recordTwo) {
        System.out.println("Sizes: " + recordOne.size() + "," + recordTwo.size());
        if (recordOne.size() != recordTwo.size()) {
            return;
        }

        matchAndReport(recordOne,recordTwo,(one,two) -> one.getMethod() + " <=> " + two.getMethod());

        matchAndReport(recordOne,recordTwo,(one,two) -> {
            String method = one.getMethod();
            String report = one.reportDiff(two);
            if (report != null) {
               return String.format("%s => %s", method, report);
            }
            return null;
        });


    }

    private static List<Played> filterUnwanted(List<Played> recordOne) {
        return recordOne.stream().filter(p -> !("com.vemod.service.VemodServiceImpl".equals(p.getService()))).collect(Collectors.toList());
    }

    private static void deleteJavaLogg(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("delete from " + COPITO_DATABASE_TABLE_PREFIX + "java_logg")) {
            statement.executeUpdate();
            connection.commit();
        }
    }

    private static List<Played> getPlayed(Connection connection) throws SQLException {
        List<Played> recordOne;
        recordOne = new ArrayList<>();
        try (PreparedStatement stmnt = connection.prepareStatement("select SERVICE, METHOD, PARAMETERS, RESULT from " + COPITO_DATABASE_TABLE_PREFIX + "java_logg order by timest")) {
            ResultSet resultSet = stmnt.executeQuery();
            while (resultSet.next()) {
                String service = resultSet.getString(1);
                String method = resultSet.getString(2);
                String parameters = resultSet.getString(3);
                String result = resultSet.getString(4);

                recordOne.add(new Played(service,method,parameters,result));

            }
        }
        return recordOne;
    }



    private static void runit(List<ReportObject> script) {
        HttpPlayer httpPlayer = new HttpPlayer("http://localhost:21110/wimpel");
        List<PlayStep> playBook = script.stream().map(ro -> new PlayStep(ro)).collect(Collectors.toList());
        httpPlayer.addManipulator(new HiddenFieldManipulator("oracle.adf.faces.STATE_TOKEN"));

        httpPlayer.play(playBook);
    }
}
