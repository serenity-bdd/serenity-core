package net.thucydides.core.pages.integration;


import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.MockEnvironmentVariables;
import net.thucydides.core.webdriver.WebDriverFactory;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;

import static net.thucydides.core.webdriver.SupportedWebDriver.CHROME;

@Ignore("Disabling temporarily due to chromedriver bug")
public class UsingWebdriverActionsWithTheFluentElementAPI extends FluentElementAPITestsBaseClass {

    static WebDriver driver;
    static StaticSitePage page;

    @BeforeClass
    public static void openStaticPage() {
        EnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setProperty("driver.options","--headless");
        driver = new WebDriverFactory().withEnvironmentVariables(environmentVariables).newInstanceOf(CHROME);
        page = new StaticSitePage(driver);
        page.setWaitForTimeout(750);
        page.open();
    }

    @Test
    public void should_be_able_to_use_the_action_api() {
        page.withAction().moveToElement(page.firstName).perform();
    }

    @Test
    public void should_be_able_to_use_the_mouse_actions_using_the_webdriver() {
        Actions builder = new Actions(page.getDriver());
        builder.moveToElement(page.firstName).perform();
    }

    @Test
    public void should_be_able_to_use_keyboard_actions_using_the_webdriver() {
        Actions builder = new Actions(page.getDriver());
        builder.moveToElement(page.firstName).sendKeys("Joe").perform();
    }
}
