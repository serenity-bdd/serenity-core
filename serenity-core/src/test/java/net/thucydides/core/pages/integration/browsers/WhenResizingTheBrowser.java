package net.thucydides.core.pages.integration.browsers;


import net.thucydides.core.pages.integration.StaticSitePage;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.MockEnvironmentVariables;
import net.thucydides.core.webdriver.SupportedWebDriver;
import net.thucydides.core.webdriver.WebDriverFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WhenResizingTheBrowser {

    private StaticSitePage page;
    private EnvironmentVariables environmentVariables;
    private WebDriver driver;
    WebDriverFactory factory;

    @Before
    public void setupFactory() {
        environmentVariables = new MockEnvironmentVariables();
        factory = new WebDriverFactory(environmentVariables);
    }

    @After
    public void shutdownDriver() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void should_resize_browser_automatically() {
        environmentVariables.setProperty("thucydides.browser.height", "200");
        environmentVariables.setProperty("thucydides.browser.width", "400");

        driver = factory.newInstanceOf(SupportedWebDriver.PHANTOMJS);
        page = new StaticSitePage(driver, 1000);
        page.open();

        Dimension screenSize = driver.manage().window().getSize();
        assertThat(screenSize.width, is(400));
        assertThat(screenSize.height, is(200));
    }
}
