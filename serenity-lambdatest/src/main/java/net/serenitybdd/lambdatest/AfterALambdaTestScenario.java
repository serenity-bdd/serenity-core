package net.serenitybdd.lambdatest;

import net.serenitybdd.core.webdriver.RemoteDriver;
import net.serenitybdd.core.webdriver.enhancers.AfterAWebdriverScenario;
import net.thucydides.core.model.ExternalLink;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.SessionId;

import static net.thucydides.core.model.TestResult.FAILURE;

public class AfterALambdaTestScenario implements AfterAWebdriverScenario {

    @Override
    public void apply(EnvironmentVariables environmentVariables, TestOutcome testOutcome, WebDriver driver) {
        if (!LambdaTestConfiguration.isActiveFor(environmentVariables)) {
            return;
        }
        // Update the test outcome if the test failed
        if (testOutcome.getResult().isAtLeast(FAILURE)) {
            ((JavascriptExecutor) driver).executeScript("lambda-status=failed");
        }

        // Set the link to the video
        SessionId sessionId = RemoteDriver.of(driver).getSessionId();
        String publicUrl = LambdaTestVideoLink.forEnvironment(environmentVariables).videoUrlForSession(sessionId.toString());
        testOutcome.setLink(new ExternalLink(publicUrl, "LambdaTest"));

    }

    public boolean isActivated(EnvironmentVariables environmentVariables) {
        return LambdaTestConfiguration.isActiveFor(environmentVariables);
    }
}
