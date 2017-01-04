package net.thucydides.core.webdriver;

import net.serenitybdd.core.webdriver.configuration.RestartBrowserForEach;
import org.openqa.selenium.WebDriver;

public class DeactivatedCloseBrowser implements CloseBrowser {
    @Override
    public void closeIfConfiguredForANew(RestartBrowserForEach event) {}

    @Override
    public void closeWhenTheTestsAreFinished(WebDriver driver) {}

    @Override
    public CloseBrowser forTestSuite(Class<?> testSuite) {
        return this;
    }
}
