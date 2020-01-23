package net.serenitybdd.browserstack;

import com.google.gson.Gson;
import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.serenitybdd.core.webdriver.RemoteDriver;
import net.serenitybdd.core.webdriver.enhancers.AfterAWebdriverScenario;
import net.thucydides.core.model.ExternalLink;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.steps.BaseStepListener;
import net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;


public class AfterABrowserStackScenario implements AfterAWebdriverScenario {

    private static final Logger LOGGER = LoggerFactory.getLogger(AfterABrowserStackScenario.class);

    private static final String BROWSERSTACK_URL_LINK = "https://api.browserstack.com/automate/builds/%s/sessions/%s.json";

    private Gson gson = new Gson();

    @Override
    public void apply(EnvironmentVariables environmentVariables, TestOutcome testOutcome, WebDriver driver) {
        if ((driver == null) || (!RemoteDriver.isARemoteDriver(driver))) {
            return;
        }

        try {
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

        } catch (URISyntaxException | IOException e) {
            LOGGER.error("Failed to update BrowserStack",e);
        }
    }

}