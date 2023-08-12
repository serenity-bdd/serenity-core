package net.serenitybdd.core.photography;

import net.serenitybdd.annotations.BlurLevel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class ScreenshotNegative {
    private final Path temporaryPath;
    private final Path screenshotPath;
    private final BlurLevel blurLevel;

    public ScreenshotNegative(Path temporaryPath, Path screenshotPath, BlurLevel blurLevel) {
        this.temporaryPath = temporaryPath;
        this.screenshotPath = screenshotPath;
        this.blurLevel = Optional.ofNullable(blurLevel).orElse(BlurLevel.NONE);
    }

    public Path getTemporaryPath() {
        return temporaryPath;
    }

    public Path getScreenshotPath() {
        return screenshotPath;
    }

    public BlurLevel getBlurLevel() {
        return blurLevel;
    }

    public static ScreenshotNegativeBuilder prepareNegativeIn(Path screenshotsDirectory) {
        return new ScreenshotNegativeBuilder(screenshotsDirectory);
    }

    public ScreenshotNegative withScreenshotPath(Path path) {
        return new ScreenshotNegative(temporaryPath, path, blurLevel);
    }

    public static class ScreenshotNegativeBuilder {

        private final Path screenshotsDirectory;
        private byte[] screenshotData;
        private BlurLevel blurLevel = BlurLevel.NONE;

        public ScreenshotNegativeBuilder(Path screenshotsDirectory) {
            this.screenshotsDirectory = screenshotsDirectory;
        }

        public ScreenshotNegativeBuilder withScreenshotData(byte[] screenshotData) {
            this.screenshotData = screenshotData;
            return this;
        }


        public ScreenshotNegative andTargetPathOf(Path finalScreenshotPath) throws IOException {
            Files.createDirectories(screenshotsDirectory);
            Path screenshotWorkingFile = Files.createTempFile(screenshotsDirectory, "screenshot-", "");
            Files.write(screenshotWorkingFile, screenshotData);
            return new ScreenshotNegative(screenshotWorkingFile, finalScreenshotPath, blurLevel);
        }

        public ScreenshotNegativeBuilder andBlurringOf(BlurLevel blurLevel) {
            this.blurLevel = blurLevel;
            return this;
        }
    }

}
