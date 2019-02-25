package net.thucydides.core.pages.integration;


import net.thucydides.core.webdriver.WebDriverFacade;
import net.thucydides.core.webdriver.WebDriverFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

public class UsingWebdriverActionsWithTheFluentElementAPI extends FluentElementAPITestsBaseClass {

    static WebDriver driver;
    static StaticSitePage page;

    @BeforeClass
    public static void openStaticPage() {
        driver = new WebDriverFacade(PhantomJSDriver.class, new WebDriverFactory());
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
