package net.serenitybdd.core.photography;

import net.thucydides.core.environment.SystemEnvironmentVariables;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.WebDriverFacade;
import net.thucydides.core.webdriver.WebDriverFactory;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.IOException;

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
