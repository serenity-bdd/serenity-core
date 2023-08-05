package net.thucydides.core.webdriver.redimension;

import io.appium.java_client.AppiumDriver;
import net.thucydides.model.ThucydidesSystemProperty;
import net.thucydides.model.util.EnvironmentVariables;
import org.openqa.selenium.WebDriver;

class RedimensionConfiguration {

    private final EnvironmentVariables environmentVariables;

    RedimensionConfiguration(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public boolean supportsScreenResizing(final WebDriver driver) {
        return (supportsResizing(driver.getClass())) && isNotAMocked(driver);
    }

    public boolean isBrowserDimensionsSpecified() {
        return (getWidth() > 0) || (getHeight() > 0);
    }

    public boolean isBrowserMaximised() {
        return ThucydidesSystemProperty.SERENITY_BROWSER_MAXIMIZED.booleanFrom(environmentVariables, false);
    }

    public boolean isBrowserFullScreen() {
        return ThucydidesSystemProperty.SERENITY_BROWSER_FULL_SCREEN.booleanFrom(environmentVariables, false);
    }

    public boolean supportsResizing(Class<? extends WebDriver> driverClass) {
        return !( (AppiumDriver.class.isAssignableFrom(driverClass)));
    }

    private boolean isNotAMocked(WebDriver driver) {
        return (!(driver.getClass().getName().contains("Mock") || driver.toString().contains("Mock for")));
    }

    public int getWidth() {
        return ThucydidesSystemProperty.SERENITY_BROWSER_WIDTH.integerFrom(environmentVariables,0);
    }

    public int getHeight() {
        return ThucydidesSystemProperty.SERENITY_BROWSER_HEIGHT.integerFrom(environmentVariables, 0);
    }

    public boolean isDisabled() {
        return !ThucydidesSystemProperty.SERENITY_BROWSER_RESIZING.booleanFrom(environmentVariables, true);
    }
}
