package net.serenitybdd.core.webdriver.integration;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.PageUrls;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.core.pages.WebElementFacadeImpl;
import net.thucydides.core.configuration.SystemPropertiesConfiguration;
import net.thucydides.core.util.MockEnvironmentVariables;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.webelements.RadioButtonGroup;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.interactions.Coordinates;
import org.openqa.selenium.support.FindBy;

import java.io.File;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNotNull;

public class WhenBrowsingAWebSiteUsingPageObjects {

    public class IndexPage extends PageObject {

        public WebElement multiselect;

        public WebElementFacade checkbox;

        public WebElement color;

        @FindBy(css = "#multiselect option")
        public List<WebElement> options;

        public WebElementFacade firstname;

        @FindBy(id="firstname")
        public WebElementFacade firstnameWithFindBy;

        @FindBy(name="radio")
        public List<WebElement> radioButtons;

        public RadioButtonGroup nameRadioButtons = new RadioButtonGroup(radioButtons);

        @net.serenitybdd.core.annotations.findby.FindBy(id="firstname")
        public WebElement firstnameWithExtendedFindBy;

        @net.serenitybdd.core.annotations.findby.FindBy(css="#firstname")
        public WebElementFacade firstnameFacadeWithExtendedFindBy;

        @FindBy(name = "specialField")
        public WebElementFacade extra;

        @net.serenitybdd.core.annotations.findby.FindBy(name = "specialField")
        public WebElementFacade extraWithExtendedFindBy;

        @net.serenitybdd.core.annotations.findby.FindBy(ngModel = "angularField")
        public WebElementFacade ngModelField;

        WebElementFacade checkbox() {
            return element(checkbox);
        }

        public IndexPage(WebDriver driver, int timeout) {
            super(driver, timeout);
        }
    }

    public class IndexPageWithShortTimeout extends PageObject {

        public WebElement checkbox;

        public IndexPageWithShortTimeout(WebDriver driver, int timeout) {
            super(driver, timeout);
        }
    }

    IndexPage indexPage;

    MockEnvironmentVariables environmentVariables;

    Configuration configuration;

    static WebDriver driver;

    @BeforeClass
    public static void openDriver() {
        driver =  new HtmlUnitDriver(BrowserVersion.CHROME, true);
    }

    @Before
    public void openLocalStaticSite() {
        openStaticTestSite();
        indexPage = new IndexPage(driver, 1);
        indexPage.setWaitForTimeout(100);
    }

    @AfterClass
    public static void closeDriver() {
    	if (driver != null) {
	        driver.close();
	        driver.quit();
    	}
    }

    @Before
    public void initConfiguration() {
        environmentVariables = new MockEnvironmentVariables();
        configuration = new SystemPropertiesConfiguration(environmentVariables);
    }

    private void openStaticTestSite() {
        File baseDir = new File(System.getProperty("user.dir"));
        File testSite = new File(baseDir, "src/test/resources/static-site/index.html");
        driver.get("file://" + testSite.getAbsolutePath());
    }

    @Test
    public void should_find_page_title() {
        assertThat(indexPage.getTitle(), is("Thucydides Test Site"));
    }

    @Test
    public void should_print_web_element_facades_in_a_readable_form() {

        assertThat(indexPage.checkbox().toString(), startsWith("IndexPage.checkbox"));
    }

    @Test
    public void should_print_web_element_facade_without_a_webelement_in_a_readable_form() {

        WebElementFacade WebElement = WebElementFacadeImpl.wrapWebElement(driver, null, 0, 0);
        assertThat(WebElement.toString(), is("<Undefined web element>"));
    }

    @Test
    public void should_find_text_contained_in_page() {
        indexPage.shouldContainText("Some test pages");
    }

    @Test(expected = NoSuchElementException.class)
    public void should_not_find_text_not_contained_in_page() {
        indexPage.shouldContainText("This text is not in the pages");
    }
    
    @Test
    public void should_return_selected_value_in_select() {

        indexPage.selectMultipleItemsFromDropdown(indexPage.multiselect, "Label 2");
        String selectedValue = indexPage.getSelectedValueFrom(indexPage.multiselect);
        assertThat(selectedValue, is("2"));
    }

    @Test
    public void should_return_selected_label_in_select() {

        indexPage.selectMultipleItemsFromDropdown(indexPage.multiselect, "Label 2");
        String selectedLabel = indexPage.getSelectedLabelFrom(indexPage.multiselect);
        assertThat(selectedLabel, is("Label 2"));
    }

    @Test
    public void should_be_able_to_move_the_cursor_to_an_element() {
        indexPage.withAction().moveToElement(indexPage.checkbox).click().perform();
        assertThat(indexPage.checkbox.isSelected(), is(true));
    }

    @Test
    public void should_be_able_to_check_the_coordinates_of_an_element() {
        Coordinates coordinates = indexPage.checkbox.getCoordinates();
        assertThat(coordinates, is(not(nullValue())));
    }

    @Test
    public void should_select_values_in_select() {
        indexPage.selectFromDropdown(indexPage.color, "Red");
        assertThat(indexPage.getSelectedOptionValuesFrom(indexPage.color), hasItem("red"));
    }

    @Test
    public void ticking_an_empty_checkbox_should_set_the_value_to_true() {
        indexPage.setCheckbox(indexPage.checkbox, true);

        assertThat(indexPage.checkbox.isSelected(), is(true));
    }

    @Test
    public void should_select_radio_buttons_by_value() {
        indexPage.nameRadioButtons.selectByValue("2");
        assertThat(indexPage.nameRadioButtons.getSelectedValue().get(), is("2"));
    }

    @Test
    public void ticking_a_set_checkbox_should_set_the_value_to_true() {
        if (indexPage.checkbox.isSelected()) {
            indexPage.checkbox.click();
        }

        indexPage.setCheckbox(indexPage.checkbox, true);

        assertThat(indexPage.checkbox.isSelected(), is(true));
    }

    @Test
    public void unticking_an_unset_checkbox_should_set_the_value_to_false() {

        indexPage.setCheckbox(indexPage.checkbox, false);

        assertThat(indexPage.checkbox.isSelected(), is(false));
    }

    @Test
    public void should_be_able_to_test_if_a_checkbox_is_checked() {
        indexPage.setCheckbox(indexPage.checkbox, true);
        assertThat(indexPage.checkbox.isSelected(), is(true));
    }

    @Test
    public void should_be_able_to_test_if_a_checkbox_is_not_checked() {
        assertThat(indexPage.checkbox.isSelected(), is(false));
    }

    @Test
    public void unticking_a_set_checkbox_should_set_the_value_to_false() {
        if (indexPage.checkbox.isSelected()) {
            indexPage.checkbox.click();
        }

        indexPage.setCheckbox(indexPage.checkbox, false);
        assertThat(indexPage.checkbox.isSelected(), is(false));
    }


    @Test
    public void should_know_when_text_appears_on_a_page() {

        indexPage.waitForTextToAppear("Label 1");
    }

    @Test
    public void should_know_when_an_element_is_visible() {
        indexPage.getDriver().navigate().refresh();
        assertThat(indexPage.isElementVisible(By.id("visible")), is(true));
    }

    @Test(expected = TimeoutException.class)
    public void should_fail_if_text_does_not_appear_on_a_page() {

        indexPage.waitForTextToAppear("Label that is not present");
    }

    @Test
    public void should_know_when_one_of_several_texts_appears_on_a_page() {
        indexPage.waitForAnyTextToAppear("Label 1", "Label that is not present");
    }

    @Test(expected = TimeoutException.class)
    public void should_fail_if_the_requested_text_is_not_on_the_page() {
        indexPage.waitForAnyTextToAppear("Label that is not present");
    }

    @Test
    public void should_know_when_all_of_a_set_of_texts_appears_on_a_page() {
        indexPage.waitForAllTextToAppear("Label 1", "Label 2");
    }

    @Test(expected = TimeoutException.class)
    public void should_fail_if_one_of_a_set_of_requested_texts_does_not_appear_on_a_page() {
        indexPage.waitForAllTextToAppear("Label 1", "Label that is not present");
    }

    @Test(expected = TimeoutException.class)
    public void should_fail_if_none_of_the_requested_texts_appear_on_a_page() {
        indexPage.waitForAllTextToAppear("Label that is not present", "Another label that is not present");
    }

    @Test
    public void should_initialize_a_web_element_facade_by_name_or_id() {
        assertNotNull(indexPage.firstname);
        assertThat(indexPage.firstname.getValue(), is("<enter first name>"));
    }
//
//    @Test
//    public void should_support_legacy_web_element_facades() {
//        assertNotNull(indexPage.legacyFirstname);
//        assertThat(indexPage.legacyFirstname.getValue(), is("<enter first name>"));
//    }

    @Test
    public void should_initialize_a_web_element_facade_using_the_normal_findby_annotation() {
        assertNotNull(indexPage.firstnameWithFindBy);
        assertThat(indexPage.firstnameWithFindBy.getValue(), is("<enter first name>"));
    }

    @Test
    public void should_initialize_a_web_element_using_the_extended_findby_annotation() {
        assertNotNull(indexPage.firstnameWithExtendedFindBy);
        assertThat(indexPage.firstnameWithExtendedFindBy.getAttribute("value"), is("<enter first name>"));
    }

    @Test
    public void should_initialize_a_web_element_facade_using_the_extended_findby_annotation() {
        assertNotNull(indexPage.firstnameFacadeWithExtendedFindBy);
        assertThat(indexPage.firstnameFacadeWithExtendedFindBy.getValue(), is("<enter first name>"));
    }

    @Test
    public void should_initialize_a_list_of_web_elements() {
        assertNotNull(indexPage.options);
        assertThat(indexPage.options.size(), is(5));
    }

    @Test
    public void should_initialize_a_web_element_facade_by_annotation() {
        assertNotNull(indexPage.extra);
        assertThat(indexPage.extra.getValue(), is("Special"));
    }

    @Test
    public void should_initialize_a_web_element_facade_by_extended_annotation() {
        assertNotNull(indexPage.extraWithExtendedFindBy );
        assertThat(indexPage.extraWithExtendedFindBy.getValue(), is("Special"));
    }

    @Test
    public void should_initialize_a_web_element_facade_using_the_ngmodel_field() {
        assertNotNull(indexPage.ngModelField);
        assertThat(indexPage.ngModelField.getValue(), is("Model value"));
    }


    @Test
    public void the_page_can_be_read_from_a_file_on_the_classpath() {

        IndexPageWithShortTimeout indexPage = new IndexPageWithShortTimeout(driver, 1);

        assertThat(indexPage.getTitle(), is("Thucydides Test Site"));
    }

    @Test
    public void the_page_can_be_opened_using_an_unsecure_certificates_compatible_profile() {

        environmentVariables.setProperty("webdriver.driver", "firefox");
        environmentVariables.setProperty("refuse.untrusted.certificates", "true");

        IndexPageWithShortTimeout indexPage = new IndexPageWithShortTimeout(driver, 1);
        PageUrls pageUrls = new PageUrls(indexPage, configuration);
        indexPage.setPageUrls(pageUrls);

        assertThat(indexPage.getTitle(), is("Thucydides Test Site"));
    }

    public class FluentPage extends PageObject {

        public WebElement state;

        public FluentPage(WebDriver driver) {
            super(driver);
        }

        public void setState(String stateValue) {
            fluent().fill("#state").with(stateValue);
        }

        public String getStateValue() {
            return $(state).getValue();
        }
    }


    @Test
    public void the_page_should_support_fluentlenium() {
        File baseDir = new File(System.getProperty("user.dir"));
        File testSite = new File(baseDir, "src/test/resources/static-site/index.html");
        driver.get("file://" + testSite.getAbsolutePath());

        FluentPage page = new FluentPage(driver);
        page.setState("NSW");
        assertThat(page.getStateValue(), is("NSW"));
    }
}
