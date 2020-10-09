package net.thucydides.core.pages.integration;


import net.thucydides.core.pages.WebElementFacade;
import net.thucydides.core.webdriver.WebDriverFacade;
import net.thucydides.core.webdriver.WebDriverFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;



public class UsingTheWebElementFacade extends FluentElementAPITestsBaseClass {

    static WebDriver localDirver;
    static StaticSitePageWithFacades page;

    @BeforeClass
    public static void openStaticPage() {

        localDirver = new WebDriverFacade(HtmlUnitDriver.class, new WebDriverFactory());
        page = new StaticSitePageWithFacades(localDirver, 1);
        page.setWaitForTimeout(750);
        page.open();
    }

    @AfterClass
    public static void shutdown() {
        localDirver.quit();
    }

    @Test
    public void should_report_if_element_is_visible() {
        assertThat(page.firstName.isVisible()).isTrue();
    }

    @Test
    public void should_allow_nested_queries_on_webelements() {
        assertThat(page.clients.findBy(".firstname").getText()).isEqualTo("Tim");
    }

    @Test
    public void should_allow_nested_queries_on_legacy_webelements() {
        assertThat(page.legacyClients.findBy(".firstname").getText()).isEqualTo("Tim");
    }

    @Test
    public void should_allow_nested_queries_returning_lists_on_webelements() {
        assertThat(page.clients.thenFindAll(".firstname")).hasSize(3);
    }

    @Test
    public void queries_should_return_new_webelementfacades() {
        net.serenitybdd.core.pages.WebElementFacade element = page.clients.findBy(".firstname");
        assertThat(element.getText()).isEqualTo("Tim");
    }

    @Test
    public void new_webelementfacade_queries_should_be_interchangable_with_legacy_ones() {
        WebElementFacade legacyElement = page.clients.findBy(".firstname");
        assertThat(legacyElement.getText()).isEqualTo("Tim");
    }

    @Test
    public void page_level_queries_should_return_new_webelementfacades() {
        net.serenitybdd.core.pages.WebElementFacade element = page.find(By.cssSelector("#clients"));
        assertThat(element.isVisible()).isTrue();
    }

    /**
     * Legacy code that uses Lists of the legacy WebElementFacades will need to be updated.
     */
    @Test
    public void page_level_queries_should_return_elements_using_successive_filters() {
        List<net.serenitybdd.core.pages.WebElementFacade> elements = page.findNestedElements("#demo", "#firstname");
        assertThat(elements).isNotEmpty();
    }

    @Test
    public void page_level_queries_should_return_elements_using_successive_filters_as_a_stream() {
        Stream<net.serenitybdd.core.pages.WebElementFacade> elements = page.findEach("#demo", "#firstname");
        assertThat(elements.collect(Collectors.toList())).isNotEmpty();
    }

    @Test
    public void page_level_queries_should_return_elements_using_successive_filters_of_locatorsas_a_stream() {
        Stream<net.serenitybdd.core.pages.WebElementFacade> elements = page.findEach(By.id("demo"),By.id("firstname"));
        assertThat(elements.collect(Collectors.toList())).isNotEmpty();
    }

    @Test
    public void page_level_queries_should_return_individual_elements_using_successive_filters() {
        WebElementFacade element = page.findNested("#demo", "#firstname");
        element.shouldBePresent();
    }

    @Test
    public void page_level_queries_should_return_new_webelementfacade_lists() {
        List<net.serenitybdd.core.pages.WebElementFacade> element = page.findAll("#firstname");
        assertThat(element).isNotEmpty();
    }

    @Test
    public void page_level_queries_should_be_compatible_with_legacy_webelementfacades() {
        WebElementFacade legacyElement = page.findBy("#clients");
        assertThat(legacyElement.isVisible()).isTrue();
    }

    /**
     * Make sure we can write tests with the new web elements that use legacy page objects.
     */
    @Test
    public void new_webelementfacade_fields_should_be_assignable_from_legacy_ones() {
        net.serenitybdd.core.pages.WebElementFacade legacyElement = page.firstName;
        assertThat(legacyElement.isVisible()).isTrue();
    }

    @Test
    public void should_allow_nested_queries_returning_lists_on_legacy_webelements() {
        assertThat(page.legacyClients.thenFindAll(".firstname")).hasSize(3);
    }

    @Test
    public void should_support_legacy_web_element_facades() {
        assertThat(page.legacyFirstName.isVisible()).isTrue();
    }

    @Test
    public void should_assert_if_element_is_visible() {
        page.firstName.shouldBeVisible();
    }

    @Test(expected = AssertionError.class)
    public void should_assert_if_element_is_not_visible() {
        page.hiddenField.shouldBeVisible();
    }

    @Test
    public void should_check_if_element_is_not_visible() {
        page.hiddenField.shouldNotBeVisible();
    }

    @Test
    public void should_report_if_element_is_not_visible() {
        assertThat(page.hiddenField.isVisible()).isFalse();
    }

    @Test
    public void should_report_if_element_is_present() {
        assertThat(page.firstName.isPresent()).isTrue();
    }

    @Test
    public void should_report_if_element_is_present_but_not_visible() {
        assertThat(page.hiddenField.isPresent()).isTrue();
    }

    @Test
    public void should_find_by_css_selectors() {
        page.demoForm.findBy("#firstname").shouldBePresent();
    }

    @Test
    public void should_find_by_xpath() {
        page.demoForm.findBy("//*[@id='firstname']").shouldBePresent();
    }

    @Test
    public void should_find_multiple_elements_by_css_selectors() {
        assertThat(page.demoForm.thenFindAll(By.cssSelector("select option"))).hasSize(12);
    }

    @Test
    public void should_find_multiple_nested_elements_by_css_selectors() {
        assertThat(page.demoForm.then(By.cssSelector("#multiselect"))
                .thenFindAll("option"))
                .hasSize(5);
    }

    @Test
    public void should_find_multiple_nested_elements_by_webdriver_selectors() {
        assertThat(page.demoForm.find(By.cssSelector("#multiselect"))
                .thenFindAll(By.cssSelector("option")))
                .hasSize(5);
    }

    @Test
    public void should_find_multiple_nested_elements_by_chained_css_selectors() {
        assertThat(page.demoForm.then("#multiselect")
                .thenFindAll(By.cssSelector("option")))
                .hasSize(5);
    }

    @Test
    public void should_allow_fluent_method_connectors() {
        assertThat(page.demoForm.and().then().findBy("#multiselect")
                .thenFindAll(By.cssSelector("option")))
                .hasSize(5);
    }

    @Test
    public void should_report_if_element_is_not_present() {
        assertThat(page.fieldDoesNotExist.isPresent()).isFalse();
    }

    @Test
    public void should_return_false_if_element_is_not_present_when_asking_for_its_clickable_state() {
        assertThat(page.fieldDoesNotExist.isClickable()).isFalse();
    }

    @Test
    public void should_pass_if_expected_element_is_present() {
        page.firstName.shouldBePresent();
    }

    @Test
    public void should_pass_if_expected__if_element_is_present_but_not_visible() {
        page.hiddenField.shouldBePresent();
    }

    @Test(expected = AssertionError.class)
    public void should_throw_exception_if_element_is_not_present() {
        page.fieldDoesNotExist.shouldBePresent();
    }

    @Test
    public void should_pass_if_unexpected_element_is_not_present() {
        page.fieldDoesNotExist.shouldNotBePresent();
    }

    @Test(expected = AssertionError.class)
    public void should_throw_exception_if_unexpected_element_is_present() {
        page.doesNotExist.shouldBePresent();
    }

    @Test(timeout = 1000)
    public void should_report_element_as_not_visible_quickly_if_not_present_right_now() {
        assertThat(page.fieldDoesNotExist.isCurrentlyVisible()).isFalse();
    }

    @Test(timeout = 1000, expected = AssertionError.class)
    public void should_check_element_as_not_visible_quickly_if_not_present_right_now() {
        page.fieldDoesNotExist.shouldBeCurrentlyVisible();
    }

    public void should_check_element_as_visible_quickly_if_not_present_right_now() {
        page.firstName.shouldBeCurrentlyVisible();
    }

    @Test(timeout = 1000, expected = AssertionError.class)
    public void should_throw_expection_fast_if_unrequired_element_is_present() {
        page.firstName.shouldNotBeCurrentlyVisible();
    }

    @Test
    public void should_check_element_as_invisible_quickly_if_present_right_now() {
        page.hiddenField.shouldNotBeCurrentlyVisible();
    }

}
