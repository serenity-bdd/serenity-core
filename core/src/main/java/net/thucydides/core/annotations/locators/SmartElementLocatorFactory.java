package net.thucydides.core.annotations.locators;

import io.appium.java_client.AppiumDriver;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.MobilePlatform;
import net.thucydides.core.webdriver.appium.AppiumConfiguration;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;

import java.lang.reflect.Field;

public final class SmartElementLocatorFactory implements ElementLocatorFactory {
    private final WebDriver webDriver;
    private int timeoutInSeconds;
    private MobilePlatform platform;

    public SmartElementLocatorFactory(WebDriver webDriver, int timeoutInSeconds) {
        this.webDriver = webDriver;
        this.timeoutInSeconds = timeoutInSeconds;
        this.platform = platformFor(webDriver);

    }

    private MobilePlatform platformFor(WebDriver webDriver) {
        if (webDriver instanceof AppiumDriver) {
            AppiumConfiguration appiumConfiguration = AppiumConfiguration.from(
                    Injectors.getInjector().getProvider(EnvironmentVariables.class).get());
            return appiumConfiguration.getTargetPlatform();
        }
        return MobilePlatform.NONE;
    }

    public ElementLocator createLocator(Field field) {
        // FIXME: Need to pass through the appium platform either here, or in both ElementLocator instances
        return new SmartAjaxElementLocator(webDriver, field, platform, timeoutInSeconds);
    }


}
