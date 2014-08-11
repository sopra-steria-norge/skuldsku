package no.steria.skuldsku.testrunner.interfacerunner;

import au.com.bytecode.opencsv.CSVReader;
import no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder.RecordObject;
import no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder.RecordedDataMock;
import no.steria.skuldsku.recorder.logging.RecorderLog;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.WatchService;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static no.steria.skuldsku.testrunner.DbToFileExporter.*;

/**
 * This class reads interface interaction recordings from file, and creates RecordedDataMocks, which it serializes and
 * writes to files to a directory specified with the environment variable no.steria.skuldsku.recordedInterfaceData.
 * <p>
 * It also contains a method that will block while waiting for the file to be renamed. This is because the recorder will
 * rename the file as soon as it has finished reading the serialized data and initialized the mocks for playback. When this
 * is ready, HTTP playbacks may be started.
 */
public class StreamInterfacePlayBack {

    private String fileDestination;

    public void prepareMocks(CSVReader reader) throws IOException, ClassNotFoundException {
        String[] next = reader.readNext();
        while (!next[0].equals(JAVA_INTERFACE_RECORDINGS_HEADER)) {
            next = reader.readNext();
        }

        Map<String, List<RecordObject>> recordingByService = getRecordingsByService(reader);
        List<RecordedDataMock> recordedDataMocks = createRecordedDataMocks(recordingByService);
        fileDestination = getOrCreateFileDestination();
        writeMocksToFile(recordedDataMocks);
    }

    /**
     * Basically waits for any file in the designated directory to change, this should only be recorder renaming the datafile
     * to *.read, unless you are messing around in the directory.
     *
     * @throws IOException
     */
    public void waitForFileToBePickedUp(int secondsTimeout) throws IOException {
        WatchService watcher = FileSystems.getDefault().newWatchService();
        new File(new File(fileDestination).getParent()).toPath().register(watcher, ENTRY_CREATE, ENTRY_MODIFY);
        try {
            watcher.poll(secondsTimeout, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        watcher.close();
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
        if (fileDestination == null) {
            RecorderLog.error("Could not create file for transferring recordings to application, mocks will not play back.");
            return;
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream(fileDestination + "/RecordedInterfaceData.dta" + Math.random());
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            objectOutputStream.writeObject(recordedDataMocks);
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getOrCreateFileDestination() throws IOException {
        String recordingsDirectoryPath = System.getenv("no.steria.skuldsku.recordedInterfaceData");
        if (recordingsDirectoryPath == null) {
            recordingsDirectoryPath = System.getenv("java.io.tmpdir") + "RecordedInterfaceData";
            File recordingsDir = new File(recordingsDirectoryPath);
            boolean success = recordingsDir.mkdir();
            if (!success && !recordingsDir.exists()) {
                throw new IOException("Could not create temporary directory " + recordingsDirectoryPath +
                " will not be able to play back interface recordings.");
            }

            System.setProperty("no.steria.skuldsku.recordedInterfaceData", recordingsDirectoryPath);
        }
        return recordingsDirectoryPath;
    }
}
