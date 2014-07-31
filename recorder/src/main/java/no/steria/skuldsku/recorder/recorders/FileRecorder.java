package no.steria.skuldsku.recorder.recorders;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class FileRecorder extends StreamRecorderCommunicator {
    public FileRecorder(String filename) {
        super(toOs(filename));
    }

    private static OutputStream toOs(String filename) {
        return null;
    }
}
