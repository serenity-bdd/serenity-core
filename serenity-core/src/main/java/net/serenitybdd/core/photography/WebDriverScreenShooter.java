package net.serenitybdd.core.photography;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.IOException;

/**
 * The default screenshot mechanism, using the standard WebDriver screenshot functionality
 */
public class WebDriverScreenShooter implements ScreenShooter {
    private final WebDriver driver;

    public WebDriverScreenShooter(PhotoLens lens) {
        this.driver = ((WebDriverPhotoLens) lens).getDriver();
    }

    @Override
    public byte[] takeScreenshot() throws IOException {
        return (driver instanceof TakesScreenshot) ? ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES) : new byte[]{};
    }
}
