package net.thucydides.core.pages.integration.browsers;


import net.thucydides.core.pages.integration.FluentElementAPITestsBaseClass;
import net.thucydides.core.pages.integration.StaticSitePage;
import org.apache.commons.exec.OS;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WaitingForElementsWithTheFluentElementAPIInARealBrowser extends FluentElementAPITestsBaseClass {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static WebDriver driver;

    private static StaticSitePage staticPage;

    @BeforeClass
    public static void openBrowsers() {
        driver = new FirefoxDriver();
        staticPage = new StaticSitePage(driver, 1000);
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

        StaticSitePage page = getFirefoxPage();
        page.getDriver().navigate().refresh();

        assertThat(page.firstName.getAttribute("value"), is("<enter first name>"));

        page.element(page.firstName).typeAndEnter("joe");

        assertThat(page.firstName.getAttribute("value"), is("joe"));
    }

    @Test
    public void should_optionally_type_tab_after_entering_text_on_linux() {

        if (runningOnLinux()) {
            StaticSitePage page = getChromePage();

            assertThat(page.firstName.getAttribute("value"), is("<enter first name>"));

            page.element(page.firstName).typeAndTab("joe");

            assertThat(page.element(page.lastName).hasFocus(), is(true));
        }
    }

    @Test
    public void should_trigger_blur_event_when_focus_leaves_field_in_chrome() {
        // Not supported on Windows
        if (!OS.isFamilyWindows()) {
            StaticSitePage page = getChromePage();
            page.getDriver().navigate().refresh();

            assertThat(page.firstName.getAttribute("value"), is("<enter first name>"));

            assertThat(page.focusmessage.getText(), is(""));

            page.element(page.firstName).typeAndTab("joe");

            assertThat(page.focusmessage.getText(), is("focus left firstname"));
        }
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
