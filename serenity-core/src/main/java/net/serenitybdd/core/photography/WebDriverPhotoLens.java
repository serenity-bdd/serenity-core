package net.serenitybdd.core.photography;

import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.serenitybdd.core.webdriver.IsMobile;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.model.util.EnvironmentVariables;
import net.thucydides.core.webdriver.WebDriverFacade;
import net.thucydides.core.webdriver.WebDriverFactory;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.time.Duration;

import static net.thucydides.model.ThucydidesSystemProperty.WEBDRIVER_WAIT_FOR_ALERT_TIMEOUT;

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
        if (IsMobile.driver(driver)) { return false; } // No alerts for mobile devices
        if (driver.switchTo() == null) { return false; } // alerts not supported by the driver

        try{
            return new WebDriverWait(driver, Duration.ofSeconds(
                EnvironmentSpecificConfiguration.from(environmentVariables)
                    .getOptionalInteger(String.valueOf(WEBDRIVER_WAIT_FOR_ALERT_TIMEOUT))
                    .orElse(0))).until(ExpectedConditions.alertIsPresent()) != null;
        }
        catch (Throwable e) {
            return false;
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
