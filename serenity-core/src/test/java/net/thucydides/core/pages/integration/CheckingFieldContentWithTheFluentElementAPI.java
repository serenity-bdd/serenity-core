package net.thucydides.core.pages.integration;


import io.github.bonigarcia.wdm.WebDriverManager;
import net.thucydides.core.webdriver.exceptions.ElementShouldBeDisabledException;
import net.thucydides.core.webdriver.exceptions.ElementShouldBeEnabledException;
import net.thucydides.core.webdriver.exceptions.ElementShouldBeInvisibleException;
import net.thucydides.core.webdriver.javascript.ByShadowDom;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.temporal.ChronoUnit;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

public class CheckingFieldContentWithTheFluentElementAPI  {

    static WebDriver localDriver;
    static StaticSitePage page;

    @BeforeClass
    public static void openStaticPage() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.setHeadless(true);
        localDriver = new ChromeDriver(options);
        page = new StaticSitePage(localDriver);
        page.open();
    }

    @Before
    public void refreshPage() {
        page.getDriver().navigate().refresh();
        page.setWaitForTimeout(5000);
        page.setImplicitTimeout(2, ChronoUnit.SECONDS);
    }

    @Test
    public void should_contain_text_passes_if_field_contains_text() {
        page.element(page.colors).shouldContainText("Red");
    }

    @Test
    public void should_contain_only_text_passes_if_field_contains_only_text() {
        page.element(page.nonEmptyLabel).shouldContainOnlyText("This div tag has text");
    }

    @Test
    public void should_contain_entry_passes_if_dropdown_contains_text() {
        page.element(page.colors).shouldContainSelectedOption("Red");
    }

    @Test(expected = AssertionError.class)
    public void should_contain_entry_fails_if_dropdown_does_not_contain_exact_text() {
        page.element(page.colors).shouldContainSelectedOption("Red\nBlue");
    }

    @Test
    public void should_find_the_list_of_select_options() {
        assertThat(page.element(page.colors).getSelectOptions(), hasItems("Red", "Blue", "Green"));
    }

    @Test
    public void should_return_an_empty_list_of_select_options_for_a_non_select_field() {
        assertThat(page.element(page.checkbox).getSelectOptions().size(), is(0));
    }

    @Test
    public void should_allow_find_as_a_synonym_for_element() {
        assertThat(page.find(By.name("demo")).then(By.name("specialField")).getValue(), is("Special"));
    }

    @Test
    public void should_allow_find_as_a_synonym_for_element_using_strings() {
        assertThat(page.findBy("#demo").then("#specialField").getValue(), is("Special"));
    }


    @Test
    public void should_contain_texts_passes_if_page_contains_all_texts() {
        page.shouldContainAllText("joe", "mary");
    }

    @Test(expected = NoSuchElementException.class)
    public void should_contain_texts_fails_if_page_does_not_contain_all_texts() {
        page.shouldContainAllText("joe", "Not appearing in this page");
    }

    @Test(expected = NoSuchElementException.class)
    public void should_contain_texts_fails_if_page_does_not_contain_any__texts() {
        page.shouldContainAllText("Not appearing either", "Not appearing in this page");
    }

    @Test
    public void should_contain_text_also_works_with_non_form_elements() {
        page.element(page.grid).shouldContainText("joe");
    }

    @Test(expected = AssertionError.class)
    public void should_contain_text_throws_exception_if_field_does_not_contain_text() {
        page.element(page.colors).shouldContainText("Magenta");
    }

    @Test(expected = AssertionError.class)
    public void should_contain_only_text_throws_exception_if_field_does_not_contain_only_text() {
        page.element(page.colors).shouldContainOnlyText("Magenta");
    }

    @Test(expected = NoSuchElementException.class)
    public void should_contain_text_throws_exception_if_element_does_not_exist() {
        page.fieldDoesNotExistShouldContainText("Magenta");
    }

    @Test
    public void should_not_contain_text_passes_if_field_does_not_contains_text() {
        page.element(page.colors).shouldNotContainText("Beans");
    }

    @Test(expected = NoSuchElementException.class)
    public void should_not_contain_text_throws_exception_if_field_is_not_found() {
        page.element(page.fieldDoesNotExist).shouldNotContainText("Beans");
    }


    @Test(expected = AssertionError.class)
    public void should_not_contain_text_throws_exception_if_field_does_contains_text() {
        page.element(page.colors).shouldNotContainText("Red");
    }

    @Test
    public void should_detect_when_a_checkbox_is_selected() {
        assertThat(page.element(page.selectedCheckbox).isSelected(), is(true));
    }

    @Test
    public void should_detect_when_a_checkbox_is_not_selected() {

        assertThat(page.element(page.checkbox).isSelected(), is(false));
    }

    @Test
    public void should_detect_when_a_radio_button_is_selected() {
        assertThat(page.element(page.radioButton1).isSelected(), is(true));
    }

    @Test
    public void should_detect_when_a_radio_button_is_not_selected() {
        assertThat(page.element(page.radioButton2).isSelected(), is(false));
    }

    @Test(expected = ElementShouldBeInvisibleException.class)
    public void should_throw_exception_if_waiting_for_field_that_does_not_disappear() {
        assertThat(page.element(page.firstName).isCurrentlyVisible(), is(true));
        page.setWaitForTimeout(500);
        page.element(page.firstName).waitUntilNotVisible();
    }

    @Test
    public void should_pass_immediately_if_waiting_for_field_that_is_present() {
        page.element(page.firstName).waitUntilVisible();
    }

    @Test(expected = ElementNotInteractableException.class)
    public void should_throw_expection_if_waiting_for_field_that_does_not_appear() {
        page.setWaitForTimeout(500);
        assertThat(page.element(page.hiddenField).isCurrentlyVisible(), is(false));

        page.element(page.hiddenField).waitUntilVisible();
    }

    @Test
    public void should_wait_for_field_to_be_disabled() throws InterruptedException {
        page.element(page.initiallyEnabled).waitUntilDisabled();
        assertThat(page.element(page.initiallyEnabled).isCurrentlyEnabled(), is(false));
    }

    @Test(expected = ElementShouldBeEnabledException.class)
    public void should_fail_if_wait_for_field_to_be_enabled_never_happens() throws InterruptedException {
        page.setWaitForTimeout(500);
        page.element(page.readonlyField).waitUntilEnabled();
    }

    @Test
    public void should_pass_immediages_if_waiting_for_a_non_form_field_to_be_enabled() {
        page.element(page.placetitle).waitUntilEnabled();
    }

    @Test(expected = ElementShouldBeDisabledException.class)
    public void should_fail_if_wait_for_field_to_be_disabled_never_happens() throws InterruptedException {
        page.setWaitForTimeout(500);
        page.element(page.firstName).waitUntilDisabled();
    }

    @Test
    public void should_pass_immediately_if_wait_for_field_to_be_disabled_is_already_disabled() throws InterruptedException {
        page.element(page.readonlyField).waitUntilDisabled();
    }

    @Test
    public void is_currently_enabled_should_be_false_for_an_inexistant_element() throws InterruptedException {
        assertThat(page.element(page.fieldDoesNotExist).isCurrentlyEnabled(), is(false));
    }

    @Test
    public void is_currently_enabled_should_be_false_for_an_inexistant_web_element_facade() throws InterruptedException {
        assertThat(page.fieldDoesNotExist.isCurrentlyEnabled(), is(false));
    }

    @Test
    public void should_wait_for_field_to_be_enabled() throws InterruptedException {
        assertThat(page.element(page.initiallyDisabled).isCurrentlyEnabled(), is(false));

        page.element(page.initiallyDisabled).waitUntilEnabled();

        assertThat(page.element(page.initiallyDisabled).isCurrentlyEnabled(), is(true));
    }

    @Test
    public void should_wait_for_field_to_appear() {
        page.element(page.city).waitUntilVisible();
        assertThat(page.element(page.city).isCurrentlyVisible(), is(true));
    }

    @Test
    public void should_succeed_if_waiting_for_an_existing_field_to_appear() {
        assertThat(page.element(page.firstName).isCurrentlyVisible(), is(true));
        page.element(page.firstName).waitUntilVisible();
    }


    @Test(expected = TimeoutException.class)
    public void should_timeout_if_wait_for_text_in_element_to_dissapear_fails() {
        page.setWaitForTimeout(200);
        page.waitForTextToDisappear(page.colors, "Red");
    }

    @Test(expected = TimeoutException.class)
    public void should_timeout_when_waiting_for_elements_to_dissapear() {
        page.waitForTextToDisappear("A visible title", 500);
    }

    @Test
    public void should_select_dropdown_by_visible_text() {
        page.element(page.colors).selectByVisibleText("Blue");
        assertThat(page.element(page.colors).getSelectedVisibleTextValue(), is("Blue"));
    }

    @Test
    public void should_select_dropdown_by_value() {
        page.element(page.colors).selectByValue("blue");
        assertThat(page.element(page.colors).getSelectedValue(), is("blue"));
    }

    @Test
    public void should_find_elements_in_shadow_dom() {
        assertThat(page.find(ByShadowDom.of("#myid")).getValue(), is("shadowInputValue"));
    }

}
