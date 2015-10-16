package net.serenitybdd.core.photography;

import java.nio.file.Path;

public class ScreenshotReceipt {
    private final Path destinationPath;

    public ScreenshotReceipt(Path destinationPath) {
        this.destinationPath = destinationPath;
    }

    public Path getDestinationPath() {
        return destinationPath;
    }
}
