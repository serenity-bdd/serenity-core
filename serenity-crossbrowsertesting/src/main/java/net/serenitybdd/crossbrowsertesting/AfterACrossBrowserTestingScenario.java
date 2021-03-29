package net.serenitybdd.crossbrowsertesting;

import serenitymodel.net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import serenitycore.net.serenitybdd.core.webdriver.RemoteDriver;
import serenitycore.net.serenitybdd.core.webdriver.enhancers.AfterAWebdriverScenario;
import serenitymodel.net.thucydides.core.model.ExternalLink;
import serenitymodel.net.thucydides.core.model.TestOutcome;
import serenitymodel.net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.WebDriver;

public class AfterACrossBrowserTestingScenario implements AfterAWebdriverScenario {

    @Override
    public void apply(EnvironmentVariables environmentVariables, TestOutcome testOutcome, WebDriver driver) {
        if ((driver == null) || (!RemoteDriver.isARemoteDriver(driver))) {
            return;
        }

        String sessionId = RemoteDriver.of(driver).getSessionId().toString();
        String userName = EnvironmentSpecificConfiguration.from(environmentVariables)
                .getOptionalProperty("crossbrowsertesting.user")
                .orElse(null);

        String key = EnvironmentSpecificConfiguration.from(environmentVariables)
                .getOptionalProperty("crossbrowsertesting.key")
                .orElse(null);

        CrossBrowserTestingTestSession crossBrowserTestingTestSession = new CrossBrowserTestingTestSession(userName, key, sessionId);
        String publicUrl = crossBrowserTestingTestSession.getPublicUrl();

        testOutcome.setLink(new ExternalLink(publicUrl, "CrossBrowserTesting"));
        crossBrowserTestingTestSession.takeSnapshot("Final State");
        crossBrowserTestingTestSession.updateTestResultsFor(testOutcome);
    }
}
