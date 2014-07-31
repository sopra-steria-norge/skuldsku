package no.steria.skuldsku.testrunner.interfacerunner;

import au.com.bytecode.opencsv.CSVReader;
import no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder.MockRegistration;
import no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder.RecordObject;
import no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder.RecordedDataMock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import static no.steria.skuldsku.testrunner.DbToFileExporter.*;

public class StreamInterfacePlayBack {

    public void play(InputStream recordingStream) throws IOException, ClassNotFoundException {
        InputStreamReader in = new InputStreamReader(recordingStream);
        BufferedReader bufferedReader = new BufferedReader(in);

        CSVReader reader = new CSVReader(bufferedReader, ',', '"');
        String[] nextLine;
        String[] next = reader.readNext();
        while (!next[0].equals(JAVA_INTERFACE_RECORDINGS_HEADER)) {
            next = reader.readNext();
        }
        Map<Class, List<RecordObject>> recordings = new HashMap<>();
        while ((nextLine = reader.readNext()) != null && !nextLine[0].equals(HTTP_RECORDINGS_HEADER)) {
            if (nextLine.length == ANT_COLUMNS_JAVA_INTERFACE_RECORDINGS) {
                RecordObject recordObject = new RecordObject(nextLine[0], nextLine[1], nextLine[2], nextLine[3]);
                Class<?> serviceClass = Class.forName(nextLine[0]);
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

        Set<Class> classes = recordings.keySet();
        for (Class serviceClass : classes) {
            registerMock(serviceClass, recordings.get(serviceClass));
        }
    }

    void registerMock(Class serviceClass, List<RecordObject> recordings) {
        RecordedDataMock recordedDataMock = new RecordedDataMock(recordings);
        MockRegistration.registerMock(serviceClass, recordedDataMock);
    }

}
