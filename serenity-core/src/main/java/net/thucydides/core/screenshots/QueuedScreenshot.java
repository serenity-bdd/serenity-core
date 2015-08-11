package net.thucydides.core.screenshots;

import java.io.File;

public class QueuedScreenshot {

    private final File destinationFilename;
    private final File sourceFilename;

    public QueuedScreenshot(File sourceFilename, File destinationFilename) {
        this.sourceFilename = sourceFilename;
        this.destinationFilename = destinationFilename;
    }

    public File getDestinationFile() {
        return destinationFilename;
    }

    public File getSourceFile() {
        return sourceFilename;
    }
}
