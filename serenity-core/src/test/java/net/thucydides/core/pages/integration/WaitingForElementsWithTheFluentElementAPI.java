package net.thucydides.core.pages.integration;


import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WaitingForElementsWithTheFluentElementAPI extends FluentElementAPITestsBaseClass {

    @Test
    public void should_obtain_text_value_from_input() {
        StaticSitePage page = getPage();
        page.getDriver().navigate().refresh();
        assertThat(page.element(page.firstName).getValue(), is("<enter first name>"));
    }

    @Test
    public void should_wait_for_element_to_be_visible_and_enabled_before_clicking() {
        StaticSitePage page = getPage();
        page.open();
        page.element(page.checkbox).click();

    }

    @Test
    public void should_be_able_to_build_composite_wait_until_disabled_clauses() throws InterruptedException {
        StaticSitePage page = getPage();

        page.waitForCondition().until(page.twoFieldsAreDisabled());
    }

    @Test
    public void should_wait_for_text_to_dissapear() {
        StaticSitePage page = getPage();

        page.waitForTextToDisappear("Dissapearing text");
        assertThat(page.containsText("Dissapearing text"), is(false));
    }


    @Test
    public void contains_text_should_fail_if_text_is_not_present() {
        StaticSitePage page = getPage();
        assertThat(page.containsText("Not present"), is(false));
    }

    @Test
    public void contains_text_should_fail_if_text_is_invisible() {
        StaticSitePage page = getPage();
        assertThat(page.containsText("Invisible text"), is(false));
    }

    @Test
    public void should_wait_for_text_in_element_to_dissapear() {
        StaticSitePage page = getPage();
        page.waitForTextToDisappear(page.dissapearingtext, "Dissapearing text");

        assertThat(page.containsText("Dissapearing text"), is(false));
    }

    @Test
    public void should_wait_for_field_to_be_enabled_using_alternative_style() throws InterruptedException {
        StaticSitePage page = getPage();

        page.firstName().waitUntilVisible();
        page.firstName().waitUntilEnabled();
    }

}
