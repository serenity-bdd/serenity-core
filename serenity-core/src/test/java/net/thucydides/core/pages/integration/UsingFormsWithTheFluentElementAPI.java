package net.thucydides.core.pages.integration;


import org.assertj.core.api.Assertions;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class UsingFormsWithTheFluentElementAPI extends FluentElementAPITestsBaseClass {

    static WebDriver localDriver;
    StaticSitePage page;

    @BeforeClass
    public static void openDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1200");
        localDriver = new ChromeDriver(options);
    }

    @AfterClass
    public static void closeDriver() {
        if (localDriver != null) {
            localDriver.quit();
        }
    }

    @Before
    public void openPage() {
        page = new StaticSitePage(localDriver, 1);
        page.setWaitForTimeout(750);
        page.open();
    }

    @Test
    public void should_detect_focus_on_input_fields() {
        assertThat(page.element(page.lastName).hasFocus(), is(true));
    }

    @Test
    public void should_find_web_elements_using_selectors() {
        assertThat(page.element(By.id("lastname")).getText(), is(""));
    }


    @Test
    public void should_be_able_to_view_a_field_with_a_configured_timeout() {
        Assertions.assertThat(page.country.isVisible()).isTrue();
    }

    @Test
    public void should_be_able_to_select_a_radio_button_value() {
        page.inRadioButtonGroup("radio").selectByValue("1");
        assertThat(page.inRadioButtonGroup("radio").getSelectedValue().get(), is("1"));
    }

    @Test
    public void should_find_web_elements_using_raw_css_selector() {
        assertThat(page.element("#textField").getText(), is("text value"));
    }

    @Test
    public void should_find_web_elements_using_raw_xpath_selector() {
        assertThat(page.element("//textarea[@id='textField']").getText(), is("text value"));
    }

    @Test
    public void should_detect_focus_on_input_fields_using_page_API() {
        assertThat(page.hasFocus(page.lastName), is(true));
    }

    @Test
    public void should_detect_lack_of_focus_on_input_fields() {
        assertThat(page.element(page.firstName).hasFocus(), is(false));
    }

    @Test
    public void should_obtain_text_value_from_input() {
        assertThat(page.element(page.firstName).getValue(), is("<enter first name>"));
    }

    @Test
    public void should_obtain_text_value_from_text_area() {
        assertThat(page.element(page.textField).getText(), is("text value"));
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

    @Test(timeout = 1000)
    public void should_report_element_as_not_currently_visible_if_field_is_hidden_using_css_display_none() {
        assertThat(page.element(page.csshiddenfield).isCurrentlyVisible(), is(false));
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
