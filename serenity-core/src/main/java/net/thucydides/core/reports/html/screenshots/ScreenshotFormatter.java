package net.thucydides.core.reports.html.screenshots;

import net.thucydides.core.images.ResizableImage;
import net.thucydides.core.model.Screenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Class designed to help resize and scale screenshots to a format that is compatible with the Thucydides reports.
 */
public class ScreenshotFormatter {

    private final Screenshot screenshot;
    private final File sourceDirectory;
    private final boolean shouldKeepOriginalScreenshots;

    private static final Logger LOGGER = LoggerFactory.getLogger(ScreenshotFormatter.class);

    private ScreenshotFormatter(final Screenshot screenshot,
                                final File sourceDirectory,
                                final boolean shouldKeepOriginalScreenshots) {
        this.screenshot = screenshot;
        this.sourceDirectory = sourceDirectory;
        this.shouldKeepOriginalScreenshots = shouldKeepOriginalScreenshots;
    }

    public static ScreenshotFormatter forScreenshot(final Screenshot screenshot) {
        return new ScreenshotFormatter(screenshot, null, false);
    }

    public ScreenshotFormatter inDirectory(final File sourceDirectory) {
        return new ScreenshotFormatter(screenshot, sourceDirectory, shouldKeepOriginalScreenshots);
    }


    public ScreenshotFormatter keepOriginals(boolean shouldKeepOriginalScreenshots) {
        return new ScreenshotFormatter(screenshot, sourceDirectory, shouldKeepOriginalScreenshots);
    }

    public Screenshot expandToHeight(final int targetHeight) throws IOException {
        File screenshotFile = new File(sourceDirectory, screenshot.getFilename());
        if (screenshotFile.exists()) {
            File resizedFile = resizedImage(screenshotFile, targetHeight);
            return new Screenshot(resizedFile.getName(),
                                  screenshot.getDescription(),
                                  screenshot.getWidth(),
                                  screenshot.getError());
        } else {
            return screenshot;
        }
    }

    private File resizedImage(File screenshotFile, int maxHeight) throws IOException {
        LOGGER.info("Resizing image " + screenshotFile);
        String resizedScreenshotFilename = "scaled_" + screenshotFile.getName();
        ResizableImage scaledImage = ResizableImage.loadFrom(screenshotFile).rescaleCanvas(maxHeight);

        File scaledFile = new File(sourceDirectory, resizedScreenshotFilename);
        scaledImage.saveTo(scaledFile);
        LOGGER.info("Scaled image saved to " + scaledFile);

        if (shouldKeepOriginalScreenshots) {
            saveCopyOf(screenshotFile);
        }

        if (scaledFile.exists()) {
            LOGGER.info("Replace original screenshot with scaled version");
            Files.move(scaledFile.toPath(), screenshotFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        return screenshotFile;
    }

    private void saveCopyOf(File screenshotFile) throws IOException {
        String backupScreenshotFilename = "original_" + screenshotFile.getName();
        Files.copy(screenshotFile.toPath(), new File(sourceDirectory, backupScreenshotFilename).toPath());
    }

}

