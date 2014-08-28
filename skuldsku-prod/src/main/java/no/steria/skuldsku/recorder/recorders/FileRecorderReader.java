package no.steria.skuldsku.recorder.recorders;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileRecorderReader extends AbstractRecorderCommunicator {
    private String filename;

    public FileRecorderReader(String filename) {
        this.filename = filename;
    }


    @Override
    protected void saveRecord(String res) {
        throw new UnsupportedOperationException("This is just for reading");
    }

    @Override
    protected List<String> getRecordedRecords() {
        String recorded;
        try (InputStream is = new FileInputStream(new File(filename))) {
            recorded = toString(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String[] records = recorded.split("\n");

        List<String> result = new ArrayList<>();

        for (String record : records) {
            result.add(record);
        }

        return result;
    }

    private String toString(InputStream inputStream) throws IOException {
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
