package net.thucydides.core.pages.integration.browsers;


import net.thucydides.core.pages.integration.FluentElementAPITestsBaseClass;
import net.thucydides.core.pages.integration.StaticSitePage;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WaitingForElementsWithTheFluentElementAPIInARealBrowser extends FluentElementAPITestsBaseClass {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static WebDriver driver;

    private static StaticSitePage staticPage;

    @BeforeClass
    public static void openBrowsers() {
        driver = new ChromeDriver();
        staticPage = new StaticSitePage(driver);
        staticPage.open();
    }

    @AfterClass
    public static void quitBrowsers() {
        if (driver != null) {
            driver.quit();
        }
    }

    protected StaticSitePage getFirefoxPage() {
        return staticPage;
    }

    @Test
    public void should_optionally_type_enter_after_entering_text() {

        StaticSitePage page = getChromePage();
        page.getDriver().navigate().refresh();

        assertThat(page.firstName.getAttribute("value"), is("<enter first name>"));

        page.element(page.firstName).typeAndEnter("joe");

        assertThat(page.firstName.getAttribute("value"), is("joe"));
    }

    @Test
    public void should_be_able_to_build_composite_wait_until_enabled_clauses() throws InterruptedException {
        StaticSitePage page = getFirefoxPage();

        page.waitForCondition().until(page.firstAndLastNameAreEnabled());
    }

    @Test
    public void should_wait_for_elements_to_appear() {
        StaticSitePage page = getChromePage();
        page.waitForAnyRenderedElementOf(By.id("city"));
        assertThat(page.element(page.city).isCurrentlyVisible(), is(true));
    }


}
