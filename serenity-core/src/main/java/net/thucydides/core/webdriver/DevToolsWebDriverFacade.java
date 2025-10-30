package net.thucydides.core.webdriver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;

import java.util.Optional;

public class DevToolsWebDriverFacade extends WebDriverFacade implements HasDevTools {
    public DevToolsWebDriverFacade(Class<? extends WebDriver> driverClass, WebDriverFactory webDriverFactory) {
        super(driverClass, webDriverFactory);
    }

    public DevToolsWebDriverFacade(WebDriver driver, WebDriverFactory webDriverFactory) {
        super(driver, webDriverFactory);
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
