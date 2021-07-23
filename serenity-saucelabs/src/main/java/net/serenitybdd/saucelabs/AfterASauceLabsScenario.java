package net.serenitybdd.saucelabs;

import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.serenitybdd.core.webdriver.RemoteDriver;
import net.serenitybdd.core.webdriver.enhancers.AfterAWebdriverScenario;
import net.thucydides.core.model.ExternalLink;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AfterASauceLabsScenario implements AfterAWebdriverScenario {

    private static final Logger LOGGER = LoggerFactory.getLogger(AfterASauceLabsScenario.class);

    @Override
    public void apply(EnvironmentVariables environmentVariables, TestOutcome testOutcome, WebDriver driver) {
        if ((driver == null) || !RemoteDriver.isARemoteDriver(driver)) {
            return;
        }

        String sessionId = RemoteDriver.of(driver).getSessionId().toString();
        String userName = EnvironmentSpecificConfiguration.from(environmentVariables)
                .getOptionalProperty("saucelabs.username")
                .orElse(null);

        String key = EnvironmentSpecificConfiguration.from(environmentVariables)
                .getOptionalProperty("saucelabs.accessKey")
                .orElse(null);

        String dataCenter = EnvironmentSpecificConfiguration.from(environmentVariables)
                .getOptionalProperty("saucelabs.datacenter")
                .orElse(null);

        SauceLabsTestSession sauceLabsTestSession = new SauceLabsTestSession(dataCenter, userName, key, sessionId);
        sauceLabsTestSession.updateTestResultFor(testOutcome);

        String publicUrl = sauceLabsTestSession.getTestUrl();
        testOutcome.setLink(new ExternalLink(publicUrl, "SauceLabs"));
    }
}
