package no.steria.skuldsku.recorder.httprecorder.reporter;

import no.steria.skuldsku.recorder.httprecorder.HttpCallPersister;
import no.steria.skuldsku.recorder.httprecorder.HttpCall;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Whenever the ServletFilter processes a ServletRequest or a ServletResponse, it will create a ReportObject, and call
 * reportCall() on a CallReporter to handle the actual recording of the data.
 *
 * This specific CallReporter writes the ReportObjects to a file, with a name as specified in the constructor.
 */
public class FileHttpCallPersister implements HttpCallPersister {
    private PrintWriter writer;
    private final File givenReportFile;

    public void close() {
        writer.close();
        writer = null;
    }

    public FileHttpCallPersister(File givenReportFile) {
        this.givenReportFile = givenReportFile;
    }

    @Override
    public void initialize() {
        try {
            FileWriter fw = new FileWriter(givenReportFile);
            writer = new PrintWriter(fw);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param httpCall Data that should be reported
     */
    @Override
        public void reportCall(HttpCall httpCall) {
        writer.append(httpCall.serializedString());
        writer.append("\n");
        writer.flush();
    }

    public static List<HttpCall> readReportedObjects(String filename) {
        String serializedObjects;
        try (FileInputStream fis = new FileInputStream(filename)) {
            serializedObjects = toString(fis);
        } catch (IOException  e) {
            throw new RuntimeException(e);
        }

        List<HttpCall> result = new ArrayList<>();

        for (String serializedStr : serializedObjects.split("\n")) {
            HttpCall httpCall = HttpCall.parseFromString(serializedStr);
            result.add(httpCall);
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
