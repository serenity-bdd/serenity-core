package net.thucydides.core.webdriver;

import com.google.inject.Inject;
import net.serenitybdd.core.webdriver.configuration.RestartBrowserForEach;
import net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebdriverCloseBrowser implements CloseBrowser {

    private final EnvironmentVariables environmentVariables;

    private static final Logger LOGGER = LoggerFactory.getLogger(WebDriverFacade.class);

    @Inject
    public WebdriverCloseBrowser(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    @Override
    public void closeIfConfiguredForANew(RestartBrowserForEach event) {
        if (restartBrowserForANew(event)) {
            ThucydidesWebDriverSupport.closeCurrentDrivers();
        }
    }

    private boolean restartBrowserForANew(RestartBrowserForEach event) {
        return RestartBrowserForEach.configuredIn(environmentVariables).restartBrowserForANew(event);
    }

    @Override
    public void closeWhenTheTestsAreFinished(final WebDriver driver) {
        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                if (driver != null) {
                    try {
                        driver.quit();
                    } catch (WebDriverException mostLikelyLostContactWithTheBrowser) {
                        LOGGER.debug("Failed to close a broswer: {}", mostLikelyLostContactWithTheBrowser.getMessage());
                    }
                }
            }
        });
    }
}
