package net.serenitybdd.core.photography;

import com.assertthat.selenium_shutterbug.core.PageSnapshot;
import com.assertthat.selenium_shutterbug.core.Shutterbug;
import com.assertthat.selenium_shutterbug.utils.web.ScrollStrategy;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.screenshots.BlurLevel;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.WebDriverFacade;
import net.thucydides.core.webdriver.WebDriverFactory;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static net.serenitybdd.core.photography.ScreenshotNegative.prepareNegativeIn;

public class PhotoSession {

    private final WebDriver driver;
    private final Path outputDirectory;
    private final Darkroom darkroom;
    private BlurLevel blurLevel;
    private ScrollStrategy scrollStrategy;
    private EnvironmentVariables environmentVariables;

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private static ThreadLocal<ScreenshotPhoto> previousScreenshot = new ThreadLocal<>();
    private static ThreadLocal<Long> previousScreenshotTimestamp = ThreadLocal.withInitial(() -> 0L);

    private static final String BLANK_SCREEN = "c118a2e3019c996cb56584ec6f8cd0b2be4c056ce4ae6b83de3c32c2e364cc61.png";

    public PhotoSession(WebDriver driver, Darkroom darkroom, Path outputDirectory, BlurLevel blurLevel, ScrollStrategy scrollStrategy) {
        this.driver = driver;
        this.outputDirectory = outputDirectory;
        this.blurLevel = blurLevel;
        this.darkroom = darkroom;
        this.scrollStrategy = scrollStrategy;
        this.environmentVariables = Injectors.getInjector().getInstance(EnvironmentVariables.class);

        darkroom.isOpenForBusiness();
    }

    public ScreenshotPhoto takeScreenshot() {

        if (tooSoonForNewPhoto() && previousScreenshot.get() != null) {
            return previousScreenshot.get();
        }

        byte[] screenshotData = null;

        if (WebDriverFactory.isAlive(driver) && unproxied(driver) instanceof TakesScreenshot) {
            try {
                PageSnapshot snapshot = Shutterbug.shootPage(unproxied(driver), scrollStrategy, 500);
                screenshotData = asByteArray(snapshot.getImage());
            } catch (Exception e) {
                LOGGER.warn("Failed to take screenshot", e);
                return ScreenshotPhoto.None;
            }
        }

        if (shouldIgnore(screenshotData)) {
            return ScreenshotPhoto.None;
        }

        ScreenshotPhoto photo = storedScreenshot(screenshotData);
        previousScreenshot.set(photo);
        previousScreenshotTimestamp.set(System.currentTimeMillis());

        return photo;
    }

    private WebDriver unproxied(WebDriver driver) {
        if (driver instanceof WebDriverFacade) {
            return ((WebDriverFacade) driver).getProxiedDriver();
        } else {
            return driver;
        }
    }

    private byte[] asByteArray(BufferedImage image) throws IOException {
        try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", outputStream);
            outputStream.flush();
            return outputStream.toByteArray();
        }
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
        long minimumInterval = ThucydidesSystemProperty.WEBDRIVER_MIN_SCREENSHOT_INTERVAL.integerFrom(environmentVariables, 50);
        return (System.currentTimeMillis() - previousPhotoTaken < minimumInterval);
    }

    private ScreenshotPhoto storedScreenshot(byte[] screenshotData) {
        try {
            Path screenshotPath = screenshotPathFor(screenshotData);
            ScreenshotReceipt screenshotReceipt = storeScreenshot(screenshotData, screenshotPath);
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
