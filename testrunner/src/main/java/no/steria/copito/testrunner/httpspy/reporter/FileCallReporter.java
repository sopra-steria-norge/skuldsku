package no.steria.copito.testrunner.httpspy.reporter;

import no.steria.copito.testrunner.httpspy.CallReporter;
import no.steria.copito.testrunner.httpspy.ReportObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileCallReporter implements CallReporter {
    private PrintWriter writer;

    public static FileCallReporter create(String filename) {
        FileCallReporter fileCallReporter = new FileCallReporter();
        File file = new File(filename);
        try {
            FileWriter fw = new FileWriter(file);
            fileCallReporter.writer = new PrintWriter(fw);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return fileCallReporter;
    }

    public void close() {
        writer.close();
        writer = null;
    }

    private FileCallReporter() {
    }

    @Override
    public void reportCall(ReportObject reportObject) {
        writer.append(reportObject.serializedString());
        writer.append("\n");
        writer.flush();
    }

    public static List<ReportObject> readReportedObjects(String filename) {
        String serializedObjects;
        try (FileInputStream fis = new FileInputStream(filename)) {
            serializedObjects = toString(fis);
        } catch (IOException  e) {
            throw new RuntimeException(e);
        }

        List<ReportObject> result = new ArrayList<>();

        for (String serializedStr : serializedObjects.split("\n")) {
            ReportObject reportObject = ReportObject.parseFromString(serializedStr);
            result.add(reportObject);
        }

        return result;

    }

    private static String toString(InputStream inputStream) throws IOException {
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
