package net.serenitybdd.browserstack;

import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.serenitybdd.core.webdriver.RemoteDriver;
import net.serenitybdd.core.webdriver.enhancers.AfterAWebdriverScenario;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.model.ExternalLink;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.WebDriverFacade;
import org.openqa.selenium.WebDriver;

import static net.serenitybdd.browserstack.BrowserstackFilter.isDriverEnabled;
import static net.serenitybdd.browserstack.BrowserstackFilter.isRunningAgainstBrowserstack;
import static net.thucydides.core.ThucydidesSystemProperty.WEBDRIVER_REMOTE_URL;


public class AfterABrowserStackScenario implements AfterAWebdriverScenario {

    public AfterABrowserStackScenario() {
    }

    @Override
    public void apply(EnvironmentVariables environmentVariables, TestOutcome testOutcome, WebDriver driver) {
        if (!isDriverEnabled(driver) || !isRunningAgainstBrowserstack(environmentVariables)) {
            return;
        }

        String sessionId = RemoteDriver.of(driver).getSessionId().toString();
        String userName = EnvironmentSpecificConfiguration.from(environmentVariables)
                .getOptionalProperty("browserstack.user")
                .orElse(null);

        String key = EnvironmentSpecificConfiguration.from(environmentVariables)
                .getOptionalProperty("browserstack.key")
                .orElse(null);

        BrowserStackTestSession browserStackTestSession = new BrowserStackTestSession(userName, key, sessionId);
        browserStackTestSession.updateTestResultFor(testOutcome);

        String publicUrl = browserStackTestSession.getPublicUrl();
        testOutcome.setLink(new ExternalLink(publicUrl, "BrowserStack"));
    }
}