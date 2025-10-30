package net.serenitybdd.core.webdriver.driverproviders;

import net.thucydides.core.webdriver.appium.AppiumConfiguration;
import net.thucydides.core.webdriver.stubs.AndroidWebDriverStub;
import net.thucydides.core.webdriver.stubs.IOSWebDriverStub;
import net.thucydides.core.webdriver.stubs.WebDriverStub;
import net.thucydides.model.util.EnvironmentVariables;
import org.openqa.selenium.WebDriver;

public class RemoteWebdriverStub {

    public static WebDriver from(EnvironmentVariables environmentVariables) {
        if (AppiumConfiguration.from(environmentVariables).isDefined()) {
            switch (AppiumConfiguration.from(environmentVariables).getTargetPlatform()) {
                case IOS:
                    return new IOSWebDriverStub();

                case ANDROID:
                    return new AndroidWebDriverStub();

                case NONE:
                default:
                    return new WebDriverStub();
            }
        } else  {
            return new WebDriverStub();
        }
    }
}
