package net.serenitybdd.browserstack;

import serenitymodel.net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import serenitycore.net.serenitybdd.core.webdriver.RemoteDriver;
import serenitycore.net.serenitybdd.core.webdriver.enhancers.AfterAWebdriverScenario;
import serenitymodel.net.thucydides.core.model.ExternalLink;
import serenitymodel.net.thucydides.core.model.TestOutcome;
import serenitymodel.net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AfterABrowserStackScenario implements AfterAWebdriverScenario {

    private static final Logger LOGGER = LoggerFactory.getLogger(AfterABrowserStackScenario.class);

    @Override
    public void apply(EnvironmentVariables environmentVariables, TestOutcome testOutcome, WebDriver driver) {
        if ((driver == null) || (!RemoteDriver.isARemoteDriver(driver))) {
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