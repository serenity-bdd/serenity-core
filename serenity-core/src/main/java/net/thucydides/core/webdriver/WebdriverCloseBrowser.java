package net.thucydides.core.webdriver;

import net.serenitybdd.core.di.SerenityInfrastructure;
import net.serenitybdd.core.webdriver.configuration.RestartBrowserForEach;
import net.thucydides.core.junit.SerenityJUnitTestCase;
import net.thucydides.model.domain.TestTag;
import net.thucydides.model.statistics.TestCount;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.model.util.EnvironmentVariables;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.serenitybdd.core.webdriver.configuration.RestartBrowserForEach.FEATURE;
import static net.serenitybdd.core.webdriver.configuration.RestartBrowserForEach.NEVER;

public class WebdriverCloseBrowser implements CloseBrowser {

    private final EnvironmentVariables environmentVariables;
    private static final Logger LOGGER = LoggerFactory.getLogger(WebDriverFacade.class);
    private final TestCount testCount;
    private final PeriodicRestart shouldRestartBrowser;

    public WebdriverCloseBrowser(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
        this.testCount = SerenityInfrastructure.getTestCount();
        this.shouldRestartBrowser = new PeriodicRestart(environmentVariables);
    }

    @Override
    public void closeIfConfiguredForANew(RestartBrowserForEach event) {
        if (restartBrowserForANew(event) || shouldRestartBrowserPeriodicallyNow()) {
            ThucydidesWebDriverSupport.closeCurrentDrivers();
        }
    }

    private boolean shouldRestartBrowserPeriodicallyNow() {
        return shouldRestartBrowser.forTestNumber(testCount.getCurrentTestNumber());
    }

    private boolean restartBrowserForANew(RestartBrowserForEach event) {

        // The @singlebrowser tag overrides global config
        // For @singlebrowser features scenarios don't restart the browser here - we will restart only if this is a new feature event
        if (StepEventBus.getParallelEventBus().isUniqueSession() || (StepEventBus.getParallelEventBus().currentTestHasTag(TestTag.withValue("singlebrowser")))) {
            return (event == FEATURE || event == NEVER);
        }

        // If there is no @singlebrowser tag, use the default configuration
        return RestartBrowserForEach.configuredIn(environmentVariables).restartBrowserForANew(event);
    }

    @Override
    public void closeWhenTheTestsAreFinished(final WebDriver driver) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (driver != null) {
                try {
                    driver.quit();
                } catch (WebDriverException mostLikelyLostContactWithTheBrowser) {
                    LOGGER.debug("Failed to close a browser: {}", mostLikelyLostContactWithTheBrowser.getMessage());
                }
            }
        }));
    }

    @Override
    public CloseBrowser forTestSuite(Class<?> testSuite) {
        if (testSuite == null) {
            return this;
        }
        if (SerenityJUnitTestCase.inClass(testSuite).isAWebTest()) {
            return new TestSuiteCloseBrowser(environmentVariables, testSuite);
        }
        return new DeactivatedCloseBrowser();
    }
}
