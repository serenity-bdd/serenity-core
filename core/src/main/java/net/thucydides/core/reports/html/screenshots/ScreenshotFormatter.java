package net.thucydides.core.reports.html.screenshots;

import net.thucydides.core.images.ResizableImage;
import net.thucydides.core.model.Screenshot;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Class designed to help resize and scale screenshots to a format that is compatible with the Thucydides reports.
 */
public class ScreenshotFormatter {

    private final Screenshot screenshot;
    private final File sourceDirectory;
    private final boolean shouldKeepOriginalScreenshots;

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
        String resizedScreenshotFilename = "scaled_" + screenshotFile.getName();
        ResizableImage scaledImage = ResizableImage.loadFrom(screenshotFile).rescaleCanvas(maxHeight);

        File scaledFile = new File(sourceDirectory, resizedScreenshotFilename);
        scaledImage.saveTo(scaledFile);

        if (shouldKeepOriginalScreenshots) {
            saveCopyOf(screenshotFile);
        }
        screenshotFile.delete();

        FileUtils.moveFile(scaledFile, screenshotFile);
        return screenshotFile;
    }

    private void saveCopyOf(File screenshotFile) throws IOException {
        String backupScreenshotFilename = "original_" + screenshotFile.getName();
        FileUtils.copyFile(screenshotFile, new File(sourceDirectory, backupScreenshotFilename));
    }

}

