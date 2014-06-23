package no.steria.copito.recorder.httprecorder.reporter;

import no.steria.copito.recorder.httprecorder.CallReporter;
import no.steria.copito.recorder.httprecorder.ReportObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Whenever the ServletFilter processes a ServletRequest or a ServletResponse, it will create a ReportObject, and call
 * reportCall() on a CallReporter to handle the actual recording of the data.
 *
 * This specific CallReporter writes the ReportObjects to a file, with a name as specified in the constructor.
 */
public class FileCallReporter implements CallReporter {
    private PrintWriter writer;
    private static File reportFile;

    public static FileCallReporter create(File givenReportFile) {
        FileCallReporter fileCallReporter = new FileCallReporter();
        reportFile = givenReportFile;
        try {
            FileWriter fw = new FileWriter(reportFile);
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

    /**
     *
     * @param reportObject Data that should be reported
     */
    @Override
        public void reportCall(ReportObject reportObject) {
        writer.append(reportObject.serializedString());
        writer.append("\n");
        writer.flush();
    }

    @Override
    public String getRecordedData() throws FileNotFoundException {
        Scanner scanner = new Scanner(reportFile);
        String recordedData = scanner.useDelimiter("\\Z").next();
        scanner.close();
        return recordedData;
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
