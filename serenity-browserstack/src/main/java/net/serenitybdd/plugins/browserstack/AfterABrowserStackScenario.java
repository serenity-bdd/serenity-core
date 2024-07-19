package net.serenitybdd.plugins.browserstack;

import net.serenitybdd.core.webdriver.RemoteDriver;
import net.serenitybdd.core.webdriver.enhancers.AfterAWebdriverScenario;
import net.thucydides.model.domain.ExternalLink;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.core.steps.TestContext;
import net.thucydides.model.util.EnvironmentVariables;
import org.openqa.selenium.WebDriver;


public class AfterABrowserStackScenario implements AfterAWebdriverScenario {

    public AfterABrowserStackScenario() {
    }

    @Override
    public void apply(EnvironmentVariables environmentVariables, TestOutcome testOutcome, WebDriver driver) {
        if (!BrowserStackConfiguration.isDriverEnabled(driver) || !isActivated(environmentVariables)) {
            return;
        }

        String sessionId = RemoteDriver.of(driver).getSessionId().toString();
        String userName = BrowserStackCredentials.from(environmentVariables).getUser();
        String key = BrowserStackCredentials.from(environmentVariables).getAccessKey();

        BrowserStackTestSession browserStackTestSession = new BrowserStackTestSession(userName, key, sessionId);
        browserStackTestSession.updateTestResultFor(testOutcome);

        TestContext.forTheCurrentTest().recordBrowser(browserStackTestSession.getBrowser());
        TestContext.forTheCurrentTest().recordPlatform(browserStackTestSession.getOS());
        testOutcome.setContext(TestContext.forTheCurrentTest().getContext());
        testOutcome.setSessionId(sessionId);

        String publicUrl = browserStackTestSession.getPublicUrl();
        testOutcome.setLink(new ExternalLink(publicUrl, "BrowserStack"));

    }

    public boolean isActivated(EnvironmentVariables environmentVariables) {
        return BrowserStackConfiguration.isActiveFor(environmentVariables);
    }
}
