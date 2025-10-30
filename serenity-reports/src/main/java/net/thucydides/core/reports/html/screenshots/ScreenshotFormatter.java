package net.thucydides.core.reports.html.screenshots;

import net.thucydides.model.domain.screenshots.Screenshot;
import net.thucydides.model.images.ResizableImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

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
        File resizedFile = resizedTargetFile(screenshot.getFilename());
        if (!resizedFile.exists()) {
            resizedFile = resizedImage(screenshotFile, targetHeight);
            return new Screenshot(resizedFile.getName(),
                    screenshot.getDescription(),
                    screenshot.getWidth(),
                    screenshot.getTimestamp(),
                    screenshot.getError(),
                    screenshot.getDepth());
        }
        return screenshot;
    }

    private File resizedTargetFile(String screenshotFilename) {
        return new File(sourceDirectory, "scaled_" + screenshotFilename);
    }

    private File resizedImage(File screenshotFile, int maxHeight) throws IOException {
        LOGGER.debug("Resizing image " + screenshotFile);
        File scaledFile = resizedTargetFile(screenshotFile.getName());
        if (!scaledFile.exists() && isAValidScreenshotFile(screenshotFile)) {
            ResizableImage scaledImage = ResizableImage.loadFrom(screenshotFile).rescaleCanvas(maxHeight);
            scaledImage.saveTo(scaledFile);
            LOGGER.debug("Scaled image saved to " + scaledFile);
        }
        LOGGER.debug("Resizing image done -> " + scaledFile.getAbsolutePath());
        return scaledFile;
    }

    private boolean isAValidScreenshotFile(File screenshotFile) {
        return screenshotFile.length() > 0;
    }
}

