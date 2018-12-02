package net.serenitybdd.core.photography;

import net.serenitybdd.core.time.Stopwatch;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.screenshots.BlurLevel;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.WebDriverFactory;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static net.serenitybdd.core.photography.ScreenshotNegative.prepareNegativeIn;

public class PhotoSession {

    private final WebDriver driver;
    private final Path outputDirectory;
    private final Darkroom darkroom;
    private BlurLevel blurLevel;
    private EnvironmentVariables environmentVariables;

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private static ThreadLocal<ScreenshotPhoto> previousScreenshot = new ThreadLocal<>();
    private static ThreadLocal<Long> previousScreenshotTimestamp = ThreadLocal.withInitial(() -> 0L);

    private static final String BLANK_SCREEN = "c118a2e3019c996cb56584ec6f8cd0b2be4c056ce4ae6b83de3c32c2e364cc61.png";

    public PhotoSession(WebDriver driver, Darkroom darkroom, Path outputDirectory, BlurLevel blurLevel) {
        this.driver = driver;
        this.outputDirectory = outputDirectory;
        this.blurLevel = blurLevel;
        this.darkroom = darkroom;
        this.environmentVariables = Injectors.getInjector().getInstance(EnvironmentVariables.class);

        darkroom.isOpenForBusiness();
    }

    public ScreenshotPhoto takeScreenshot() {

        if (tooSoonForNewPhoto() && previousScreenshot.get() != null) {
            return previousScreenshot.get();
        }

        ScreenshotPhoto photo;

        byte[] screenshotData = null;
        if (WebDriverFactory.isAlive(driver) && driver instanceof TakesScreenshot) {
            try {
                Stopwatch stopwatch = Stopwatch.started();
                screenshotData = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                LOGGER.info("Screenshot read in " + stopwatch.stop() + " ms");
            } catch (Exception e) {
                LOGGER.warn("Failed to take screenshot", e);
                return ScreenshotPhoto.None;
            }
        }

        if (shouldIgnore(screenshotData)) {
            return ScreenshotPhoto.None;
        }

        photo = storedScreenshot(screenshotData);
        previousScreenshot.set(photo);
        previousScreenshotTimestamp.set(System.currentTimeMillis());

        return photo;
    }

    private boolean shouldIgnore(byte[] screenshotData) {
        if ((screenshotData == null) || (screenshotData.length == 0)) {
            return true;
        }
        if (filenameFor(screenshotData).equals(BLANK_SCREEN)) {
            return true;
        }
        return false;
    }


    private boolean tooSoonForNewPhoto() {
        long previousPhotoTaken = previousScreenshotTimestamp.get();
        long minimumInterval = ThucydidesSystemProperty.WEBDRIVER_MIN_SCREENSHOT_INTERVAL.integerFrom(environmentVariables,250);
        return (System.currentTimeMillis() - previousPhotoTaken < minimumInterval);
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

        return darkroom.sendNegative(screenshotNegative);
    }

    private String filenameFor(byte[] screenshotData) {
        return ScreenshotDigest.forScreenshotData(screenshotData);
    }

    private Path screenshotPathFor(byte[] screenshotData) {
        return outputDirectory.resolve(filenameFor(screenshotData));
    }
}
