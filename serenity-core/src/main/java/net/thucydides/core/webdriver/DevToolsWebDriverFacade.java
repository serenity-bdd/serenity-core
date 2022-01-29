package net.thucydides.core.webdriver;

import net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;

import java.time.Duration;
import java.util.Optional;

public class DevToolsWebDriverFacade extends WebDriverFacade implements HasDevTools {
    public DevToolsWebDriverFacade(Class<? extends WebDriver> driverClass, WebDriverFactory webDriverFactory) {
        super(driverClass, webDriverFactory);
    }

    public DevToolsWebDriverFacade(Class<? extends WebDriver> driverClass, WebDriverFactory webDriverFactory, EnvironmentVariables environmentVariables) {
        super(driverClass, webDriverFactory, environmentVariables);
    }

    public DevToolsWebDriverFacade(WebDriver driver, WebDriverFactory webDriverFactory, EnvironmentVariables environmentVariables) {
        super(driver, webDriverFactory, environmentVariables);
    }

    public DevToolsWebDriverFacade(Class<? extends WebDriver> driverClass, WebDriverFactory webDriverFactory, WebDriver proxiedWebDriver, Duration implicitTimeout) {
        super(driverClass, webDriverFactory, proxiedWebDriver, implicitTimeout);
    }

    @Override
    public Optional<DevTools> maybeGetDevTools() {
        if (hasDevTools()) {
            return Optional.of(getDevTools());
        } else {
            return Optional.empty();
        }
    }
}
