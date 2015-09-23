package no.steria.skuldsku.testrunner.interfacerunner;

import no.steria.skuldsku.recorder.java.JavaCall;
import no.steria.skuldsku.recorder.java.mock.RecordedDataMock;
import no.steria.skuldsku.recorder.logging.RecorderLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchService;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

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

    public List<RecordedDataMock> prepareMocks(List<JavaCall> javaCalls) throws IOException, ClassNotFoundException {
        Map<String, List<JavaCall>> recordingByService = setupMocksWithCallbacks(javaCalls);
        List<RecordedDataMock> recordedDataMocks = createRecordedDataMocks(recordingByService);
        fileDestination = getOrCreateFileDestination();
        writeMocksToFile(recordedDataMocks);
        return recordedDataMocks;
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

    private Map<String, List<JavaCall>> setupMocksWithCallbacks(List<JavaCall> callbacks) throws IOException, ClassNotFoundException {
        Map<String, List<JavaCall>> recordings = new HashMap<>();
        for (JavaCall callback: callbacks) {
            List<JavaCall> recordObjects = recordings.get(callback.getClassName());
            if (recordObjects == null) {
                List<JavaCall> mockSpecificRecordObjects = new ArrayList<>();
                mockSpecificRecordObjects.add(callback);
                recordings.put(callback.getClassName(), mockSpecificRecordObjects);
            } else {
                recordObjects.add(callback);
            }
        }

        return recordings;
    }

    List<RecordedDataMock> createRecordedDataMocks(Map<String, List<JavaCall>> recordingByService) {
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
            Path recordedInterfaceData = Files.createTempDirectory("RecordedInterfaceData");
            recordingsDirectoryPath = recordedInterfaceData.toString();
            System.setProperty("no.steria.skuldsku.recordedInterfaceData", recordingsDirectoryPath);
        }
        return recordingsDirectoryPath;
    }
}
