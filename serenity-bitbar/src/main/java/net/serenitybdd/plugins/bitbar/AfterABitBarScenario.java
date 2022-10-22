package net.serenitybdd.plugins.bitbar;

import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.serenitybdd.core.webdriver.RemoteDriver;
import net.serenitybdd.core.webdriver.enhancers.AfterAWebdriverScenario;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.environment.TestLocalEnvironmentVariables;
import net.thucydides.core.model.ExternalLink;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.WebDriver;

public class AfterABitBarScenario implements AfterAWebdriverScenario {

    public AfterABitBarScenario() {
    }

    @Override
    public void apply(EnvironmentVariables environmentVariables, TestOutcome testOutcome, WebDriver driver) {
        if (!BitBarConfiguration.isDriverEnabled(driver) || !isActivated(environmentVariables)) {
            return;
        }

        String sessionId = RemoteDriver.of(driver).getSessionId().toString();
        String webdriverRemoteUrl = TestLocalEnvironmentVariables.getProperties().get(ThucydidesSystemProperty.WEBDRIVER_REMOTE_URL.getPropertyName());
        String cloudUrl = EnvironmentSpecificConfiguration.from(environmentVariables).getOptionalProperty("bitbar.cloudUrl").orElse("https://cloud.bitbar.com");
        String apiKey = BitBarCredentials.from(environmentVariables).getApiKey();

        BitBarTestSession bitBarTestSession = new BitBarTestSession(cloudUrl, webdriverRemoteUrl, apiKey, sessionId);
        bitBarTestSession.updateTestResultFor(testOutcome);

        String publicUrl = bitBarTestSession.getPublicUrl();
        testOutcome.setLink(new ExternalLink(publicUrl, "BitBar"));
    }

    public boolean isActivated(EnvironmentVariables environmentVariables) {
        return BitBarConfiguration.isActiveFor(environmentVariables);
    }
}
