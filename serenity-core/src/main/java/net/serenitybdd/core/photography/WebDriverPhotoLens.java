package net.serenitybdd.core.photography;

import net.thucydides.core.environment.SystemEnvironmentVariables;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.WebDriverFacade;
import net.thucydides.core.webdriver.WebDriverFactory;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.UnsupportedCommandException;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.util.Set;

/**
 * Take a screenshot with a specified WebDriver instance.
 * By default screenshots will be taken using standard Selenium.
 * You can override the default behaviour by setting the screenshot.shooter property
 */
public class WebDriverPhotoLens implements PhotoLens {
    private final WebDriver driver;
    private final EnvironmentVariables environmentVariables;

    public WebDriverPhotoLens(WebDriver driver) {
        this.driver = driver;
        this.environmentVariables = SystemEnvironmentVariables.currentEnvironmentVariables();
    }

    public WebDriver getDriver() {
        return driver;
    }

    @Override
    public boolean canTakeScreenshot() {
        if (alertIsDisplayedFor(driver)) {
            return false;
        }
        return (WebDriverFactory.isAlive(driver) && unproxied(driver) instanceof TakesScreenshot);
    }

    private boolean alertIsDisplayedFor(WebDriver driver) {
        if (driver.switchTo() == null) { return false; }

        String currentWindow = null;
        try {
            currentWindow = driver.getWindowHandle();
            driver.switchTo().alert().getText();
            return true;
        } catch (NoAlertPresentException | UnsupportedCommandException screenshotsNotSupportedIfAnAlertIsPresent) {
            return false;
        } finally {
            if (currentWindow != null) {
                driver.switchTo().window(currentWindow);
            }
        }
    }

    public byte[] takeScreenshot() throws IOException {
        byte[] screenshotData = null;

        ScreenShooterFactory screenShooterFactory = new ScreenShooterFactory(environmentVariables);
        ScreenShooter screenShooter = screenShooterFactory.buildScreenShooter(this);

        if (WebDriverFactory.isAlive(driver) && unproxied(driver) instanceof TakesScreenshot) {
            screenshotData = screenShooter.takeScreenshot();
        }

        return screenshotData;
    }

    private WebDriver unproxied(WebDriver driver) {
        if (driver instanceof WebDriverFacade) {
            return ((WebDriverFacade) driver).getProxiedDriver();
        } else {
            return driver;
        }
    }
}
