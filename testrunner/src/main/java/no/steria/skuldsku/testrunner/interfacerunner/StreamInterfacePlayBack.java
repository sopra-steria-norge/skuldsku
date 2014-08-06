package no.steria.skuldsku.testrunner.interfacerunner;

import au.com.bytecode.opencsv.CSVReader;
import no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder.RecordObject;
import no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder.RecordedDataMock;
import no.steria.skuldsku.recorder.logging.RecorderLog;

import java.io.*;
import java.util.*;

import static no.steria.skuldsku.testrunner.DbToFileExporter.*;

public class StreamInterfacePlayBack {

    public void play(InputStream recordingStream) throws IOException, ClassNotFoundException {
        InputStreamReader in = new InputStreamReader(recordingStream);
        BufferedReader bufferedReader = new BufferedReader(in);

        CSVReader reader = new CSVReader(bufferedReader, ',', '"');
        String[] next = reader.readNext();
        while (!next[0].equals(JAVA_INTERFACE_RECORDINGS_HEADER)) {
            next = reader.readNext();
        }

        Map<String, List<RecordObject>> recordingByService = getRecordingsByService(reader);
        List<RecordedDataMock> recordedDataMocks = createRecordedDataMocks(recordingByService);
        writeMocksToFile(recordedDataMocks);
    }


    private Map<String, List<RecordObject>> getRecordingsByService(CSVReader reader) throws IOException, ClassNotFoundException {
        String[] nextLine;
        Map<String, List<RecordObject>> recordings = new HashMap<>();
        while ((nextLine = reader.readNext()) != null && !nextLine[0].equals(HTTP_RECORDINGS_HEADER)) {
            if (nextLine.length == ANT_COLUMNS_JAVA_INTERFACE_RECORDINGS) {
                RecordObject recordObject = new RecordObject(nextLine[0], nextLine[1], nextLine[2], nextLine[3]);
                String serviceClass = nextLine[0];
                List<RecordObject> recordObjects = recordings.get(serviceClass);
                if (recordObjects == null) {
                    List<RecordObject> mockSpecificRecordObjects = new ArrayList<>();
                    mockSpecificRecordObjects.add(recordObject);
                    recordings.put(serviceClass, mockSpecificRecordObjects);
                } else {
                    recordObjects.add(recordObject);
                }
            }
        }
        return recordings;
    }

    List<RecordedDataMock> createRecordedDataMocks(Map<String, List<RecordObject>> recordingByService) {
        List<RecordedDataMock> recordedDataMocks = new ArrayList<>();
        Set<String> classes = recordingByService.keySet();
        for (String serviceClass : classes) {
            RecordedDataMock recordedDataMock = new RecordedDataMock(recordingByService.get(serviceClass));
            recordedDataMock.setServiceClass(serviceClass);
            recordedDataMocks.add(recordedDataMock);
        }
        return recordedDataMocks;
    }

    private void writeMocksToFile(List<RecordedDataMock> recordedDataMocks) {
        String fileDestination = getOrCreateFileDestination();
        if (fileDestination == null) {
            RecorderLog.error("Could not create file for transferring recordings to application, mocks will not play back.");
            return;
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream(fileDestination);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            objectOutputStream.writeObject(recordedDataMocks);
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getOrCreateFileDestination() {
        //TODO ikh: create constant for property
        String property = System.getProperty("no.steria.skuldsku.recordedInterfaceData");
        if (property == null) {
            try {
                property = File.createTempFile("recordedInterfaceData", ".dta").getAbsolutePath();
                System.setProperty("no.steria.skuldsku.recordedInterfaceData", property);
            } catch (IOException e) {
                RecorderLog.error("Could not create temporary file for recorded interface data. Interface data will not be played back", e);
                e.printStackTrace();
                return null;
            }
        }
        return property;
    }

}
