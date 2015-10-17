package net.serenitybdd.core.photography;

import net.thucydides.core.screenshots.BlurLevel;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static net.serenitybdd.core.photography.ScreenshotNegative.prepareNegativeIn;

public class PhotoSession {

    private final WebDriver driver;
    private final Path outputDirectory;
    private BlurLevel blurLevel;

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public PhotoSession(WebDriver driver, Path outputDirectory, BlurLevel blurLevel) {
        this.driver = driver;
        this.outputDirectory = outputDirectory;
        this.blurLevel = blurLevel;
        Darkroom.isOpenForBusiness();
    }

    public ScreenshotPhoto takeScreenshot() {

        byte[] screenshotData = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);

        if (screenshotData == null || screenshotData.length == 0) {
            return ScreenshotPhoto.None;
        }

        return storedScreenshot(screenshotData);
    }

    private ScreenshotPhoto storedScreenshot(byte[] screenshotData) {
        try {
            Path screenshotPath = screenshotPathFor(screenshotData);
            ScreenshotReceipt screenshotReceipt = storeScreenshot(screenshotData, screenshotPath);
            LOGGER.debug("Screenshot scheduled to be saved to {}", screenshotPath);
            return ScreenshotPhoto.forScreenshotAt(screenshotReceipt.getDestinationPath());
        } catch (IOException e) {
            LOGGER.warn("Failed to save screenshot", e);
            return ScreenshotPhoto.None;
        }
    }

    private ScreenshotReceipt storeScreenshot(byte[] screenshotData, Path screenshotPath) throws IOException {
        Path screenshotsDirectory = DarkroomFileSystem.get().getPath("/var/screenshots");

        Files.createDirectories(screenshotsDirectory);

        ScreenshotNegative screenshotNegative = prepareNegativeIn(screenshotsDirectory)
                .withScreenshotData(screenshotData)
                .andBlurringOf(blurLevel)
                .andTargetPathOf(screenshotPath);

        return Darkroom.sendNegative(screenshotNegative);
    }

    private Path screenshotPathFor(byte[] screenshotData) {
        String screenshotFilename = ScreenshotDigest.forScreenshotData(screenshotData);
        return outputDirectory.resolve(screenshotFilename);
    }
}
