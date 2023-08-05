package net.serenitybdd.plugins.saucelabs;

import net.serenitybdd.core.webdriver.RemoteDriver;
import net.serenitybdd.core.webdriver.enhancers.AfterAWebdriverScenario;
import net.serenitybdd.plugins.CapabilityTags;
import net.thucydides.model.domain.ExternalLink;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.util.EnvironmentVariables;
import org.openqa.selenium.JavascriptExecutor;
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
        if (!SauceLabsConfiguration.isActiveFor(environmentVariables)) {
            return;
        }

        String sessionId = null;
        if (RemoteDriver.of(driver).getSessionId() != null) {
            sessionId = RemoteDriver.of(driver).getSessionId().toString();
        }
        String userName = SauceLabsCredentials.from(environmentVariables).getUser();

        String key = SauceLabsCredentials.from(environmentVariables).getAccessKey();

        if (userName == null || key == null) {
            LOGGER.warn("Incomplete SauceLabs configuration" + System.lineSeparator()
                    + "SauceLabs integration needs the following system properties to work:" + System.lineSeparator()
                    + "  - saucelabs.username - Your SauceLabs account name" + System.lineSeparator()
                    + "  - saucelabs.accessKey - Your SauceLabs Access Key" + System.lineSeparator()
                    + "You can find both of these here: https://app.saucelabs.com/user-settings"
            );
        } else {
            ((JavascriptExecutor)driver).executeScript("job-build=" + BuildName.from(environmentVariables));
            ((JavascriptExecutor)driver).executeScript("job-tags=" +  CapabilityTags.tagsFrom(testOutcome, environmentVariables));

            String result = (testOutcome.isSuccess()) ? "passed" : "failed";
            ((JavascriptExecutor)driver).executeScript("job-result=" +  result);

            SauceLabsTestSession sauceLabsTestSession = new SauceLabsTestSession(userName, key, sessionId);

            String publicUrl = sauceLabsTestSession.getTestLink();
            testOutcome.setLink(new ExternalLink(publicUrl, "SauceLabs"));
        }
    }

    @Override
    public boolean isActivated(EnvironmentVariables environmentVariables) {
        return SauceLabsConfiguration.isActiveFor(environmentVariables);
    }
}
