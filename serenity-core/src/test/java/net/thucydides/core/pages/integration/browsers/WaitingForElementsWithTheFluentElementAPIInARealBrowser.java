package net.thucydides.core.pages.integration.browsers;


import net.thucydides.core.pages.integration.FluentElementAPITestsBaseClass;
import net.thucydides.core.pages.integration.StaticSitePage;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WaitingForElementsWithTheFluentElementAPIInARealBrowser extends FluentElementAPITestsBaseClass {

    StaticSitePage page;

    @Before
    public void openPage() {
        page  = getPage();
        page.open();
    }

    @Test
    public void should_be_able_to_build_composite_wait_until_enabled_clauses() throws InterruptedException {
        page.waitForCondition().until(page.firstAndLastNameAreEnabled());
    }

    @Test
    public void should_wait_for_elements_to_appear() {
        page.waitForAnyRenderedElementOf(By.id("city"));
        assertThat(page.element(page.city).isCurrentlyVisible(), is(true));
    }


}
