package net.thucydides.core.pages.integration;


import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

public class CheckingVisibilityWithTheFluentElementAPI extends FluentElementAPITestsBaseClass {

    static StaticSitePage page;


    @Before
    public void openPage() {
        page = getPage();
    }

    @Test
    public void should_report_if_element_is_visible() {
        assertThat(page.element(page.firstName).isVisible(), is(true));
    }

    @Test
    public void should_report_if_element_is_not_visible() {
        assertThat(page.element(page.hiddenField).isVisible(), is(false));
    }

    @Test
    public void should_report_if_element_is_present() {
        assertThat(page.element(page.firstName).isPresent(), is(true));
    }

    @Test
    public void should_report_if_element_is_present_but_not_visible() {
        assertThat(page.element(page.hiddenField).isPresent(), is(true));
    }

    @Test
    public void should_report_if_element_is_not_present() {
        assertThat(page.element(page.fieldDoesNotExist).isPresent(), is(false));
    }

    @Test
    public void should_pass_if_expected_element_is_present() {
        page.element(page.firstName).shouldBePresent();
    }

    @Test
    public void should_pass_if_expected__if_element_is_present_but_not_visible() {
        page.element(page.hiddenField).shouldBePresent();
    }


    @Test(expected = AssertionError.class)
    public void should_throw_exception_if_element_is_not_present() {
        page.fieldDoesNotExistShouldBePresent();
    }

    @Test
    public void should_pass_if_unexpected_element_is_not_present() {
        page.fieldDoesNotExistShouldNotBePresent();
    }

    @Test(expected = AssertionError.class)
    public void should_throw_exception_if_unexpected_element_is_present() {
        page.hiddenFieldShouldNotBePresent();
    }

    @Test
    public void should_wait_for_hidden_elements() {
        page.waitForRenderedElementsToBePresent(By.name("city"));
    }

    @Test
    public void should_wait_for_hidden_elements_using_shortened_form() {
        page.waitFor("#city");
    }

    @Test
    public void should_wait_for_hidden_elements_using_shortened_form_with_custom_timeout() {
        page.withTimeoutOf(10, TimeUnit.SECONDS).waitFor(By.cssSelector("#city"));
    }

    @Test
    public void should_wait_for_hidden_web_elements_facades() {
        page.withTimeoutOf(10, TimeUnit.SECONDS).waitFor(page.city);
    }


    @Test
    public void should_wait_for_web_elements() {
        page.withTimeoutOf(10, TimeUnit.SECONDS).waitFor(page.lastName);
    }

    @Test
    public void should_be_able_to_set_the_timeout_for_waits() {
        assertThat(page.withTimeoutOf(10, TimeUnit.SECONDS).getWaitForTimeout().in(TimeUnit.SECONDS), is(10L));
    }

    @Test
    public void wait_for_hidden_elements_should_work_for_visible_elements() {
        page.waitForRenderedElementsToBePresent(By.name("firstname"));
    }

    @Test
    public void should_wait_for_expected_condition() {
        page.waitFor(ExpectedConditions.presenceOfElementLocated(By.name("city")));
    }

    @Test
    public void wait_for_hidden_elements_should_work_for_visible_elements_using_shortened_form() {
        page.waitForPresenceOf("#firstname");
    }
    @Test(expected = TimeoutException.class)
    public void wait_for_hidden_elements_should_fail_for_missing_elements() {
        page.waitForRenderedElementsToBePresent(By.name("noSuchField"));
    }

    @Test
    public void should_report_element_as_not_visible_if_not_present() {
        assertThat(page.element(page.fieldDoesNotExist).isVisible(), is(false));
    }

    @Test(timeout = 500)
    public void should_report_element_as_not_visible_quickly_if_not_present_right_now() {
        assertThat(page.fieldDoesNotExist.isCurrentlyVisible(), is(false));
    }

    @Test(timeout = 500, expected = AssertionError.class)
    public void should_check_element_as_not_visible_quickly_if_not_present_right_now() {
        page.fieldDoesNotExist.shouldBeCurrentlyVisible();
    }


    @Test
    public void should_check_element_as_visible_quickly_if_not_present_right_now() {
        page.element(page.firstName).shouldBeCurrentlyVisible();
    }

    @Test(expected = AssertionError.class)
    public void should_throw_expection_if_required_element_is_not_present() {
        page.element(page.fieldDoesNotExist).shouldBeVisible();
    }

    @Test(expected = AssertionError.class)
    public void should_throw_expection_if_unrequired_element_is_present() {
        page.element(page.firstName).shouldNotBeVisible();
    }

    @Test(timeout = 1000, expected = AssertionError.class)
    public void should_throw_expection_fast_if_unrequired_element_is_present() {
        page.element(page.firstName).shouldNotBeCurrentlyVisible();
    }

    @Test
    public void should_check_element_as_invisible_quickly_if_present_right_now() {
        page.element(page.hiddenField).shouldNotBeCurrentlyVisible();
    }

    @Test(expected = AssertionError.class)
    public void should_throw_expection_if_required_element_is_not_visible() {
        page.element(page.hiddenField).shouldBeVisible();
    }

    @Test
    public void should_know_if_enabled_element_is_enabled() {
        assertThat(page.element(page.firstName).isEnabled(), is(true));
    }

    @Test
    public void should_be_able_to_chain_methods() {
        page.element(page.initiallyDisabled).waitUntilEnabled().and().then().click();
    }

    @Test
    public void should_know_if_disabled_element_is_not_enabled() {
        assertThat(page.element(page.readonlyField).isEnabled(), is(false));
    }

    @Test
    public void should_do_nothing_if_enabled_field_should_be_enabled() {
        page.element(page.firstName).shouldBeEnabled();
    }

    @Test(expected = AssertionError.class)
    public void should_throw_exception_if_enabled_field_should_be_disabled() {
        page.element(page.firstName).shouldNotBeEnabled();
    }

    @Test
    public void should_work_if_disabled_field_is_not_enabled() {
        page.element(page.readonlyField).shouldNotBeEnabled();
    }

    @Test(expected = AssertionError.class)
    public void should_throw_exception_if_disabled_field_should_be_enabled() {
        page.element(page.readonlyField).shouldBeEnabled();
    }

    @Test
    public void should_pass_if_unwanted_element_is_not_visible() {
        page.element(page.hiddenField).shouldNotBeVisible();
    }

    @Test
    public void should_pass_if_unwanted_element_is_not_on_page() {
        page.element(page.fieldDoesNotExist).shouldNotBeVisible();
    }

    @Test
    public void should_contain_text_passes_if_field_contains_text() {
        page.element(page.colors).shouldContainText("Red");
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
    public void should_detect_focus_on_input_fields() {
        page.evaluateJavascript("document.getElementById('lastname').focus()");
        assertThat(page.element(page.lastName).hasFocus(), is(true));
    }

    @Test
    public void should_detect_focus_on_input_fields_using_page_API() {
        page.evaluateJavascript("document.getElementById('lastname').focus()");
        assertThat(page.hasFocus(page.lastName), is(true));
    }

    @Test
    public void should_detect_lack_of_focus_on_input_fields() {
        page.evaluateJavascript("document.getElementById('lastname').focus()");
        assertThat(page.element(page.firstName).hasFocus(), is(false));
    }

    @Test
    public void should_evaluate_javascript_within_browser() {
        String result = (String) page.evaluateJavascript("return document.title");
        assertThat(result, is("Thucydides Test Site"));
    }


    @Test
    public void should_obtain_text_value_from_text_area() {
        assertThat(page.element(page.textField).getText(), is("text value"));
    }

    @Test
    public void should_execute_javascript_within_browser() {
        page.open();
        assertThat(page.element(page.firstName).hasFocus(), is(false));
        page.evaluateJavascript("document.getElementById('firstname').value='Joe'");
        assertThat(page.element(page.firstName).getValue(), is("Joe"));
    }

    @Test
    public void should_clear_field_before_entering_text() {
        page.open();

        assertThat(page.firstName.getAttribute("value"), is("<enter first name>"));

        page.element(page.firstName).type("joe");

        assertThat(page.firstName.getAttribute("value"), is("joe"));
    }

    @Test
    public void should_select_dropdown_by_visible_text() {
        page.open();

        page.element(page.colors).selectByVisibleText("Blue");
        assertThat(page.element(page.colors).getSelectedVisibleTextValue(), is("Blue"));
    }

    @Test
    public void should_select_dropdown_by_value() {
        page.open();

        page.element(page.colors).selectByValue("blue");
        assertThat(page.element(page.colors).getSelectedValue(), is("blue"));
    }

    @Test
    public void should_select_dropdown_by_index_value() {
        page.open();

        page.element(page.colors).selectByIndex(2);
        assertThat(page.element(page.colors).getSelectedValue(), is("green"));
    }

    @Test
    public void should_detect_text_contained_in_a_web_element() {
        assertThat(page.element(page.grid).containsText("joe"), is(true));
    }

    @Test
    public void should_detect_dropdown_entry_contained_in_a_web_element() {
        assertThat(page.element(page.grid).containsText("joe"), is(true));
    }

    @Test
    public void should_detect_text_not_contained_in_a_web_element() {
        assertThat(page.element(page.grid).containsText("red"), is(false));
    }

    @Test
    public void should_obtain_text_value_from_text_area_using_getTextValue() {
        assertThat(page.element(page.textField).getTextValue(), is("text value"));
    }

    @Test
    public void should_obtain_text_value_from_input_using_getTextValue() {
        assertThat(page.element(page.firstName).getTextValue(), is("<enter first name>"));
    }

    @Test
    public void should_return_empty_string_from_other_element_using_getTextValue() {
        assertThat(page.element(page.emptylist).getTextValue().trim(), is(""));
    }

    @Test
    public void should_wait_for_element_to_be_visible_and_enabled_before_clicking() {

        page.element(page.checkbox).click();

    }

    @Test
    public void should_detect_when_a_checkbox_is_selected() {
        assertThat(page.element(page.selectedCheckbox).isSelected(), is(true));
    }

    @Test
    public void should_detect_when_a_checkbox_is_not_selected() {
        page.open();
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

    @Test
    public void should_be_able_to_clear_a_text_field_using_deletes() {
        assertThat(page.firstName.getAttribute("value"), is("<enter first name>"));

        page.element(page.firstName).clear();

        assertThat(page.firstName.getAttribute("value"), is(""));
    }

    @Test
    public void should_return_empty_string_when_a_tag_does_not_have_any_text() {
        assertThat(page.element(page.emptyLabel).getTextValue(), is(""));
    }

    @Test
    public void should_return_the_actual_text_when_a_tag_has_any_text() {
        assertThat(page.element(page.nonEmptyLabel).getTextValue(), is("This div tag has text"));
    }
}
