package net.thucydides.core.webdriver.integration;

import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.MockEnvironmentVariables;
import net.thucydides.core.webdriver.SupportedWebDriver;
import net.thucydides.core.webdriver.UnsupportedDriverException;
import net.thucydides.core.webdriver.WebDriverFactory;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;

import static org.fest.assertions.Assertions.assertThat;

public class WhenInstanciatingAChromeDriver {

    private EnvironmentVariables environmentVariables;
    private String previousChromeDriverPath;

    @Before
    public void createATestableDriverFactory() throws Exception {
        previousChromeDriverPath = System.getProperty("webdriver.chrome.driver");
        environmentVariables = new MockEnvironmentVariables();
    }

    @After
    public void restoreChromeDriver() {
        if (StringUtils.isEmpty(previousChromeDriverPath)) {
            System.clearProperty("webdriver.chrome.driver");
        } else {
            System.setProperty("webdriver.chrome.driver", previousChromeDriverPath);
        }
    }

    @Test
    public void should_honor_chromdriver_bin_path_in_environment_properties() {
        environmentVariables.setProperty("webdriver.chrome.driver","/path/to/chromedriver/bin");
        try {
            WebDriver webdriver = new WebDriverFactory(environmentVariables).newInstanceOf(SupportedWebDriver.CHROME);
            webdriver.get("http://www.google.com");
        } catch (UnsupportedDriverException couldNotFindDriver) {
            assertThat(couldNotFindDriver.getCause().getMessage()).contains("The driver executable does not exist: /path/to/chromedriver/bin");
        }
    }

}
