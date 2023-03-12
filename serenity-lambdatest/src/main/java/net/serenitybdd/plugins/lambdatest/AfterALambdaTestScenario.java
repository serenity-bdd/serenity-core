package net.serenitybdd.plugins.lambdatest;

import net.serenitybdd.core.model.TestOutcomeName;
import net.serenitybdd.core.webdriver.RemoteDriver;
import net.serenitybdd.core.webdriver.enhancers.AfterAWebdriverScenario;
import net.thucydides.core.model.ExternalLink;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.steps.session.TestSession;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.SessionId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static net.thucydides.core.model.TestResult.FAILURE;

/**
 * Update the LambdaTest outcomes on the server
 */
public class AfterALambdaTestScenario implements AfterAWebdriverScenario {

    private static final Logger LOGGER = LoggerFactory.getLogger(AfterALambdaTestScenario.class);

    @Override
    public void apply(EnvironmentVariables environmentVariables, TestOutcome testOutcome, WebDriver driver) {
        if (!LambdaTestConfiguration.isActiveFor(environmentVariables)) {
            return;
        }
        SessionId sessionId = TestSession.getTestSessionContext().getWebSessionId();
        if (sessionId != null) {
            LOGGER.debug("getSessionId from TestSession " + sessionId);
        } else {
            sessionId = RemoteDriver.of(driver).getSessionId();
            LOGGER.debug("getSessionId from remoteDriver " + sessionId);
        }

        updateTestStatusFrom(testOutcome, environmentVariables, sessionId.toString());

        // Set the link to the video
        String publicUrl = LambdaTestVideoLink.forEnvironment(environmentVariables).videoUrlForSession(sessionId.toString());
        testOutcome.setLink(new ExternalLink(publicUrl, "LambdaTest"));

    }

    private static final String NAME_AND_STATUS_UPDATE = "{\"name\":\"%s\", \"status_ind\":\"%s\"}";

    private void updateTestStatusFrom(TestOutcome testOutcome, EnvironmentVariables environmentVariables, String sessionId) {
        String testName = TestOutcomeName.from(testOutcome);
        String testResult = resultFrom(testOutcome);
        LOGGER.debug("Update test result for test " + testName + " testResult " + testResult);

        try {
            StringEntity updatePayload = new StringEntity(String.format(NAME_AND_STATUS_UPDATE, testName, testResult));
            URI uri = LambdaTestUri.definedIn(environmentVariables).getSessionUri(sessionId);
            HttpPatch putRequest = new HttpPatch(uri);

            putRequest.setEntity(updatePayload);
            HttpClientBuilder.create().build().execute(putRequest);

        } catch (URISyntaxException | IOException e) {
            LOGGER.warn("Failed to update LambdaTest results", e);
        }
    }

    private String resultFrom(TestOutcome testOutcome) {
        if (testOutcome.isSuccess()) {
            return "passed";
        } else if (testOutcome.getResult().isAtLeast(FAILURE)) {
            return "failed";
        } else {
            return "completed";
        }
    }

    public boolean isActivated(EnvironmentVariables environmentVariables) {
        return LambdaTestConfiguration.isActiveFor(environmentVariables);
    }
}
