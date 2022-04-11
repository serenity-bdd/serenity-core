package net.serenitybdd.core.photography;

import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.ScreenshotException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * The default screenshot mechanism, using the standard WebDriver screenshot functionality
 */
public class WebDriverScreenShooter implements ScreenShooter {
    private final WebDriver driver;

    private final static Logger LOGGER = LoggerFactory.getLogger(WebDriverScreenShooter.class);

    public WebDriverScreenShooter(PhotoLens lens) {
        this.driver = ((WebDriverPhotoLens) lens).getDriver();
    }

    @Override
    public byte[] takeScreenshot() throws IOException {
        try {
            return (driver instanceof TakesScreenshot) ? ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES) : new byte[]{};
        } catch(Exception failedToTakeScreenshot) {
            LOGGER.warn("Failed to take screenshot - Selenium reported the following error", failedToTakeScreenshot);
            return new byte[]{};
        }
    }
}
