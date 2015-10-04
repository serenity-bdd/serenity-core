package net.serenitybdd.core.photography;

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

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public PhotoSession(WebDriver driver, Path outputDirectory) {
        this.driver = driver;
        this.outputDirectory = outputDirectory;
        Darkroom.isOpenForBusiness();
    }

    public ScreenshotPhoto takeScreenshot() {

        byte[] screenshotData = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        Path screenshotPath = screenshotPathFor(screenshotData);

        if (!Files.exists(screenshotPath)) {
            try {
                storeScreenshot(screenshotData, screenshotPath);
            } catch (IOException e) {
                LOGGER.warn("Failed to save screenshot", e);
                return ScreenshotPhoto.None;
            }
        }

        return ScreenshotPhoto.forScreenshotAt(screenshotPath);

    }

    private void storeScreenshot(byte[] screenshotData, Path screenshotPath) throws IOException {
        Path screenshotsDirectory = DarkroomFileSystem.get().getPath("./screenshots");

        ScreenshotNegative screenshotNegative = prepareNegativeIn(screenshotsDirectory)
                                                            .withScreenshotData(screenshotData)
                                                            .andFinalPathOf(screenshotPath);

        DarkroomQueue.sendNegative(screenshotNegative);


    }

    private Path screenshotPathFor(byte[] screenshotData) {
        String screenshotFilename = ScreenshotDigest.forScreenshotData(screenshotData);
        return outputDirectory.resolve(screenshotFilename);
    }
}
