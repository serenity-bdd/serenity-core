package net.thucydides.core.pages.integration.browsers;


import net.thucydides.core.pages.integration.FluentElementAPITestsBaseClass;
import net.thucydides.core.pages.integration.StaticSitePage;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WaitingForElementsWithTheFluentElementAPIInARealBrowser extends FluentElementAPITestsBaseClass {

    @Test
    public void should_optionally_type_enter_after_entering_text() {

        StaticSitePage page  = new StaticSitePage(new FirefoxDriver());
        page.open();

        assertThat(page.firstName.getAttribute("value"), is("<enter first name>"));

        page.element(page.firstName).typeAndEnter("joe");

        assertThat(page.firstName.getAttribute("value"), is("joe"));
    }

    @Test
    public void should_be_able_to_build_composite_wait_until_enabled_clauses() throws InterruptedException {
        StaticSitePage page  = new StaticSitePage(new PhantomJSDriver());
        page.open();
        page.waitForCondition().until(page.firstAndLastNameAreEnabled());
    }

    @Test
    public void should_wait_for_elements_to_appear() {
        StaticSitePage page  = new StaticSitePage(new PhantomJSDriver());
        page.open();
        page.waitForAnyRenderedElementOf(By.id("city"));
        assertThat(page.element(page.city).isCurrentlyVisible(), is(true));
    }


}
