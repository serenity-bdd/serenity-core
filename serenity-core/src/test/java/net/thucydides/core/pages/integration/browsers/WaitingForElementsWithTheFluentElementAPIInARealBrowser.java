package net.thucydides.core.pages.integration.browsers;


import net.thucydides.core.pages.integration.FluentElementAPITestsBaseClass;
import net.thucydides.core.pages.integration.StaticSitePage;
import net.thucydides.core.webdriver.ThucydidesWebDriverSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WaitingForElementsWithTheFluentElementAPIInARealBrowser extends FluentElementAPITestsBaseClass {

    private StaticSitePage staticPage;

    @Before
    public void openBrowsers() {
        ThucydidesWebDriverSupport.initialize();
        staticPage = new StaticSitePage(ThucydidesWebDriverSupport.getWebdriverManager().getWebdriver("chrome"));
        staticPage.open();
    }

    @After
    public void quitBrowsers() {
        ThucydidesWebDriverSupport.getWebdriverManager().closeAllDrivers();
    }

    protected StaticSitePage getFirefoxPage() {
        return staticPage;
    }

    @Test
    public void should_optionally_type_enter_after_entering_text() {

        staticPage.getDriver().navigate().refresh();

        assertThat(staticPage.firstName.getAttribute("value"), is("<enter first name>"));

        staticPage.element(staticPage.firstName).typeAndEnter("joe");

        assertThat(staticPage.firstName.getAttribute("value"), is("joe"));
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
