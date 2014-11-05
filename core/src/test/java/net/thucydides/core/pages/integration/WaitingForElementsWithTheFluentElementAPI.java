package net.thucydides.core.pages.integration;


import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WaitingForElementsWithTheFluentElementAPI extends FluentElementAPITestsBaseClass {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static WebDriver phantomDriver;
    private static StaticSitePage phantomPage;

    @BeforeClass
    public static void openBrowsers() {
        phantomDriver = new PhantomJSDriver();
        phantomPage = new StaticSitePage(phantomDriver, 1000);
    }

    @AfterClass
    public static void quitBrowsers() {
        if (phantomDriver != null) {
            phantomDriver.quit();
        }
    }

    protected StaticSitePage getPhantomJSPage() {
        return phantomPage;
    }
    
    @Test
    public void should_obtain_text_value_from_input() {
        StaticSitePage page = getPhantomJSPage();
        page.getDriver().navigate().refresh();
        assertThat(page.element(page.firstName).getValue(), is("<enter first name>"));
    }

    @Test
    public void should_wait_for_element_to_be_visible_and_enabled_before_clicking() {
        StaticSitePage page = getPhantomJSPage();
        page.open();
        //page.getDriver().navigate().refresh();
        page.element(page.checkbox).click();

    }

    @Test
    public void should_be_able_to_build_composite_wait_until_disabled_clauses() throws InterruptedException {
        StaticSitePage page = getPhantomJSPage();

        page.waitForCondition().until(page.twoFieldsAreDisabled());
    }

    @Test
    public void should_wait_for_text_to_dissapear() {
        StaticSitePage page = getPhantomJSPage();

        page.waitForTextToDisappear("Dissapearing text");
        assertThat(page.containsText("Dissapearing text"), is(false));
    }


    @Test
    public void contains_text_should_fail_if_text_is_not_present() {
        StaticSitePage page = getPhantomJSPage();
        assertThat(page.containsText("Not present"), is(false));
    }

    @Test
    public void contains_text_should_fail_if_text_is_invisible() {
        StaticSitePage page = getPhantomJSPage();
        assertThat(page.containsText("Invisible text"), is(false));
    }

    @Test
    public void should_wait_for_text_in_element_to_dissapear() {
        StaticSitePage page = getPhantomJSPage();
        page.waitForTextToDisappear(page.dissapearingtext, "Dissapearing text");

        assertThat(page.containsText("Dissapearing text"), is(false));
    }

    @Test
    public void should_wait_for_field_to_be_enabled_using_alternative_style() throws InterruptedException {
        StaticSitePage page = getPhantomJSPage();

        page.firstName().waitUntilVisible();
        page.firstName().waitUntilEnabled();
    }

}
