package net.serenitybdd.core.photography;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ScreenshotNegative {
    private final Path temporaryPath;
    private final Path screenshotPath;

    public ScreenshotNegative(Path temporaryPath, Path screenshotPath) {
        this.temporaryPath = temporaryPath;
        this.screenshotPath = screenshotPath;
    }

    public Path getTemporaryPath() {
        return temporaryPath;
    }

    public Path getScreenshotPath() {
        return screenshotPath;
    }

    public static ScreenshotNegativeBuilder prepareNegativeIn(Path screenshotsDirectory) {
        return new ScreenshotNegativeBuilder(screenshotsDirectory);
    }

    public static class ScreenshotNegativeBuilder {

        private final Path screenshotsDirectory;
        private byte[] screenshotData;

        public ScreenshotNegativeBuilder(Path screenshotsDirectory) {
            this.screenshotsDirectory = screenshotsDirectory;
        }

        public ScreenshotNegativeBuilder withScreenshotData(byte[] screenshotData) {
            this.screenshotData = screenshotData;
            return this;
        }


        public ScreenshotNegative andFinalPathOf(Path finalScreenshotPath) throws IOException {
            Files.createDirectories(screenshotsDirectory);
            Path screenshotWorkingFile = Files.createTempFile(screenshotsDirectory, "screenshot-", "");
            Files.write(screenshotWorkingFile, screenshotData);
            return new ScreenshotNegative(screenshotWorkingFile, finalScreenshotPath);
        }

    }

}
