package net.thucydides.core.pages.integration;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.core.pages.WebElementFacadeImpl;
import net.thucydides.core.util.MockEnvironmentVariables;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.configuration.SystemPropertiesConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class WhenManagingAPageObject {

    @Mock
    WebDriver driver;

    @Mock
    WebElement mockButton;

    MockEnvironmentVariables environmentVariables;

    Configuration configuration;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        environmentVariables = new MockEnvironmentVariables();
        configuration = new SystemPropertiesConfiguration(environmentVariables);
    }

    class BasicPageObject extends PageObject {

        protected WebElement button;

        public BasicPageObject(WebDriver driver) {
            super(driver);
        }

        protected WebElement getButton() {
            return mockButton;
        }

        public void invokeWaitABit(long time) {
            waitABit(500);
        }


    }

    @Test
    public void the_page_gets_the_title_from_the_web_page() {

        when(driver.getTitle()).thenReturn("Google Search Page");
        BasicPageObject page = new BasicPageObject(driver);

        assertThat(page.getTitle(), is("Google Search Page"));
    }

    @Test
    public void page_will_wait_for_rendered_element_if_it_is_already_present() {

        WebElement renderedElement = mock(WebElement.class);
        List<WebElement> renderedElements = new ArrayList<WebElement>();
        renderedElements.add(renderedElement);

        when(driver.findElement(any(By.class))).thenReturn(renderedElement);
        when(driver.findElements(any(By.class))).thenReturn(renderedElements);

        when(renderedElement.isDisplayed()).thenReturn(true);

        BasicPageObject page = new BasicPageObject(driver);
        page.waitForRenderedElements(By.id("whatever"));
    }


    @Test
    public void thenReturnElementList_will_return_the_list_of_matching_elements() {

        WebElement renderedElement = mock(WebElement.class);
        List<WebElement> renderedElements = new ArrayList<WebElement>();
        renderedElements.add(renderedElement);

        when(driver.findElement(any(By.class))).thenReturn(renderedElement);
        when(driver.findElements(any(By.class))).thenReturn(renderedElements);

        BasicPageObject page = new BasicPageObject(driver);
        List<WebElement> elementList = page.thenReturnElementList(By.className("whatever"));

        assertThat(elementList, is(renderedElements));
    }

    @Test
    public void page_can_delay_requests_for_a_short_period() {
        long start = System.currentTimeMillis();
        BasicPageObject page = new BasicPageObject(driver);
        page.invokeWaitABit(500);

        assertThat((int) (System.currentTimeMillis() - start), greaterThanOrEqualTo(500));
    }

    @Test
    public void page_will_wait_for_rendered_element_if_it_is_not_already_present() {

        WebElement renderedElement = mock(WebElement.class);
        List<WebElement> renderedElements = new ArrayList<WebElement>();
        renderedElements.add(renderedElement);

        when(driver.findElement(any(By.class))).thenReturn(renderedElement);
        when(driver.findElements(any(By.class))).thenReturn(renderedElements);
        when(renderedElement.isDisplayed()).thenReturn(false).thenReturn(false).thenReturn(true);

        BasicPageObject page = new BasicPageObject(driver);
        page.setWaitForTimeout(200);
        page.waitForRenderedElements(By.id("whatever"));
    }

    @Test
    public void page_will_wait_for_title_to_appear_if_requested() {

        BasicPageObject page = new BasicPageObject(driver);
        when(driver.getTitle()).thenReturn("waiting..").thenReturn("a title");

        page.waitForTitleToAppear("a title");
    }

    @Test
    public void page_will_wait_for_title_to_appear_if_already_there() {

        BasicPageObject page = new BasicPageObject(driver);
        when(driver.getTitle()).thenReturn("a title");

        page.waitForTitleToAppear("a title");
    }

    @Test
    public void page_will_wait_for_title_to_disappear_if_requested() {

        BasicPageObject page = new BasicPageObject(driver);
        when(driver.getTitle()).thenReturn("a title").thenReturn("all gone");

        page.waitForTitleToDisappear("a title");
    }

    @Test(expected = TimeoutException.class)
    public void page_will_wait_for_title_to_disappear_should_fail_if_title_doesnt_disappear() {

        BasicPageObject page = new BasicPageObject(driver);
        page.setWaitForTimeout(100);
        when(driver.getTitle()).thenReturn("a title");

        page.waitForTitleToDisappear("a title");
    }

    @Test
    public void page_will_wait_for_text_to_appear_in_element_if_requested() {

        BasicPageObject page = new BasicPageObject(driver);
        WebElement textBlock = mock(WebElement.class);
        WebElement searchedBlock = mock(WebElement.class);

        List<WebElement> emptyList = Arrays.asList();
        List<WebElement> listWithElements = Arrays.asList(textBlock);

        when(searchedBlock.findElements(any(By.class))).thenReturn(emptyList).thenReturn(listWithElements);
        when(searchedBlock.getText()).thenReturn("contains 'hi there'");

        page.waitForTextToAppear(searchedBlock, "hi there");
    }

    @Test
    public void wait_for_text_to_appear_in_element_will_succeed_if_element_is_already_present() {

        BasicPageObject page = new BasicPageObject(driver);
        WebElement textBlock = mock(WebElement.class);
        WebElement searchedBlock = mock(WebElement.class);

        List<WebElement> emptyList = Arrays.asList();
        List<WebElement> listWithElements = Arrays.asList(textBlock);

        when(searchedBlock.findElements(any(By.class))).thenReturn(listWithElements);
        when(searchedBlock.getText()).thenReturn("contains 'hi there'");

        page.waitForTextToAppear(searchedBlock, "hi there");
    }

    @Test(expected = TimeoutException.class)
    public void wait_for_text_to_appear_in_element_will_fail_if_text_does_not_appear() {

        BasicPageObject page = new BasicPageObject(driver);
        page.setWaitForTimeout(150);
        WebElement searchedBlock = mock(WebElement.class);

        List<WebElement> emptyList = Arrays.asList();

        when(searchedBlock.findElements(any(By.class))).thenReturn(emptyList);
        when(searchedBlock.getText()).thenReturn("no matching text here");

        page.waitForTextToAppear(searchedBlock, "hi there");
    }

    @Test
    public void page_will_wait_for_text_to_appear_successfully_if_already_present() {

        BasicPageObject page = new BasicPageObject(driver);
        WebElement textBlock = mock(WebElement.class);
        WebElement searchedBlock = mock(WebElement.class);

        List<WebElement> listWithElements = Arrays.asList(textBlock);

        when(searchedBlock.findElements(any(By.class))).thenReturn(listWithElements);
        when(searchedBlock.getText()).thenReturn("contains 'hi there'");

        page.waitForTextToAppear(searchedBlock, "hi there");
    }

    @Test
    public void page_will_wait_for_text_to_appear_in_an_element_if_requested() {

        BasicPageObject page = new BasicPageObject(driver);
        WebElement textBlock = mock(WebElement.class);
        WebElement searchedBlock = mock(WebElement.class);

        List<WebElement> emptyList = Arrays.asList();
        List<WebElement> listWithElements = Arrays.asList(textBlock);

        when(searchedBlock.findElements(any(By.class))).thenReturn(emptyList).thenReturn(listWithElements);
        when(searchedBlock.getText()).thenReturn("contains 'hi there'");

        page.waitForAnyTextToAppear(searchedBlock, "hi there");
    }


    @Test(expected = TimeoutException.class)
    public void page_will_fail_if_single_text_fails_to_appear_in_an_element_if_requested() {

        BasicPageObject page = new BasicPageObject(driver);
        WebElement searchedBlock = mock(WebElement.class);

        List<WebElement> emptyList = Arrays.asList();

        when(searchedBlock.findElements(any(By.class))).thenReturn(emptyList);
        when(searchedBlock.getText()).thenReturn("no matching text");

        page.setWaitForTimeout(200);
        page.waitForAnyTextToAppear(searchedBlock, "hi there");
    }

    @Test(expected = TimeoutException.class)
    public void page_will_fail_if_text_fails_to_appear_in_an_element_if_requested() {

        BasicPageObject page = new BasicPageObject(driver);
        WebElement searchedBlock = mock(WebElement.class);

        List<WebElement> emptyList = Arrays.asList();

        when(searchedBlock.findElements(any(By.class))).thenReturn(emptyList);
        when(searchedBlock.getText()).thenReturn("no matching text");

        page.setWaitForTimeout(200);
        page.waitForAnyTextToAppear(searchedBlock, "hi there");
    }

    @Test
    public void entering_a_value_in_a_field_will_clear_it_first() {
        WebElement field = mock(WebElement.class);
        BasicPageObject page = new BasicPageObject(driver);
        when(field.isEnabled()).thenReturn(true);
        when(field.getTagName()).thenReturn("input");

        page.typeInto(field, "some value");

        verify(field).clear();
        verify(field).sendKeys("some value");
    }

    @Test
    public void should_provide_a_fluent_api_for_entering_a_value_in_a_field_using_webelement() {
        WebElement field = mock(WebElement.class);
        BasicPageObject page = new BasicPageObject(driver);
        when(field.isEnabled()).thenReturn(true);
        when(field.getTagName()).thenReturn("input");

        page.enter("some value").into(field);

        verify(field).clear();
        verify(field).sendKeys("some value");
    }

    @Test
    public void should_provide_a_fluent_api_for_entering_a_value_in_a_field_using_webelementfacade() {
        WebElement field = mock(WebElement.class);
        WebElementFacade facade = WebElementFacadeImpl.wrapWebElement(driver, field, 0, 0);
        BasicPageObject page = new BasicPageObject(driver);
        when(field.isEnabled()).thenReturn(true);
        when(field.getTagName()).thenReturn("input");

        page.enter("some value").into(facade);

        verify(field).clear();
        verify(field).sendKeys("some value");
    }

    @Test
    public void should_provide_a_fluent_api_for_entering_a_value_in_a_field_using_a_selector() {
        WebElement field = mock(WebElement.class);
        when(driver.findElement(By.id("field-id"))).thenReturn(field);
        when(field.isEnabled()).thenReturn(true);
        when(field.getTagName()).thenReturn("input");
        BasicPageObject page = new BasicPageObject(driver);

        page.enter("some value").intoField(By.id("field-id"));

        verify(field).clear();
        verify(field).sendKeys("some value");
    }


    @Test(expected = TimeoutException.class)
    public void page_will_throw_exception_if_waiting_for_rendered_element_does_not_exist() {

        when(driver.findElement(any(By.class))).thenThrow(new NoSuchElementException("No such element"));

        BasicPageObject page = new BasicPageObject(driver);
        page.setWaitForTimeout(200);
        page.waitForRenderedElements(By.id("whatever"));
    }


    @Test(expected = TimeoutException.class)
    public void page_will_throw_exception_if_waiting_for_rendered_element_is_not_visible() {

        WebElement renderedElement = mock(WebElement.class);
        when(driver.findElement(any(By.class))).thenReturn(renderedElement);
        when(renderedElement.isDisplayed()).thenReturn(false);

        BasicPageObject page = new BasicPageObject(driver);
        page.setWaitForTimeout(200);
        page.waitForRenderedElements(By.id("whatever"));
    }


    @Test
    public void page_will_succeed_for_any_of_several_rendered_elements() {

        WebElement renderedElement = mock(WebElement.class);
        elementIsRendered(renderedElement, By.id("element1"));
        noElementIsRendered(By.id("element2"));

        BasicPageObject page = new BasicPageObject(driver);
        page.setWaitForTimeout(200);
        page.waitForAnyRenderedElementOf(By.id("element1"), By.id("element2"));
    }

    @Test(expected = TimeoutException.class)
    public void page_will_fail_for_any_of_several_rendered_elements_if_element_is_displayed_but_not_rendered() {

        WebElement renderedElement = mock(WebElement.class);
        elementIsDisplayedButNotRendered(renderedElement, By.id("element1"));
        noElementIsRendered(By.id("element2"));

        BasicPageObject page = new BasicPageObject(driver);
        page.setWaitForTimeout(200);
        page.waitForAnyRenderedElementOf(By.id("element1"), By.id("element2"));
    }


    @Test
    public void page_will_wait_for_any_of_several_rendered_elements() {

        WebElement renderedElement = mock(WebElement.class);
        elementIsRenderedWithDelay(renderedElement, By.id("element1"));
        noElementIsRendered(By.id("element2"));

        BasicPageObject page = new BasicPageObject(driver);
        page.setWaitForTimeout(1000);
        page.waitForAnyRenderedElementOf(By.id("element1"), By.id("element2"));
    }


    @Test(expected = TimeoutException.class)
    public void page_will_fail_if_none_of_the_several_rendered_elements_are_present() {

        noElementIsRendered(By.id("element1"));
        noElementIsRendered(By.id("element2"));


        BasicPageObject page = new BasicPageObject(driver);
        page.setWaitForTimeout(1000);
        page.waitForAnyRenderedElementOf(By.id("element1"), By.id("element2"));
    }

    private void noElementIsRendered(By criteria) {
        List<WebElement> emptyList = Arrays.asList();
        when(driver.findElement(criteria)).thenThrow(new NoSuchElementException("No such element"));
        when(driver.findElements(criteria)).thenReturn(emptyList);
    }

    private void elementIsRendered(WebElement renderedElement, By criteria) {
        when(renderedElement.isDisplayed()).thenReturn(true);
        List<WebElement> listWithRenderedElement = Arrays.asList((WebElement) renderedElement);
        when(driver.findElement(criteria)).thenReturn(renderedElement);
        when(driver.findElements(criteria)).thenReturn(listWithRenderedElement);
    }

    private void elementIsDisplayedButNotRendered(WebElement renderedElement, By criteria) {
        when(renderedElement.isDisplayed()).thenReturn(false);
        List<WebElement> listWithRenderedElement = Arrays.asList((WebElement) renderedElement);
        when(driver.findElement(criteria)).thenReturn(renderedElement);
        when(driver.findElements(criteria)).thenReturn(listWithRenderedElement);
    }

    private void elementIsRenderedWithDelay(WebElement renderedElement, By criteria) {
        List<WebElement> emptyList = Arrays.asList();

        when(renderedElement.isDisplayed()).thenReturn(false).thenReturn(true);
        List<WebElement> listWithRenderedElement = Arrays.asList((WebElement) renderedElement);
        when(driver.findElement(criteria)).thenThrow(new NoSuchElementException("No such element"))
                .thenThrow(new NoSuchElementException("No such element"))
                .thenReturn(renderedElement);
        when(driver.findElements(criteria)).thenReturn(emptyList)
                .thenReturn(listWithRenderedElement);
    }


    private void elementDisappearsAfterADelay(WebElement renderedElement, By criteria) {
        List<WebElement> emptyList = Arrays.asList();

        when(renderedElement.isDisplayed()).thenReturn(true).thenReturn(false);
        List<WebElement> listWithRenderedElement = Arrays.asList((WebElement) renderedElement);
        when(driver.findElement(criteria)).thenReturn(renderedElement)
                .thenReturn(renderedElement)
                .thenThrow(new NoSuchElementException("No such element"));
        when(driver.findElements(criteria)).thenReturn(listWithRenderedElement)
                .thenReturn(listWithRenderedElement)
                .thenReturn(emptyList);
    }


    @Test(expected = AssertionError.class)
    public void should_be_visible_should_throw_an_assertion_if_element_is_not_visible() {
        BasicPageObject page = new BasicPageObject(driver);
        WebElement field = mock(WebElement.class);
        when(field.isDisplayed()).thenReturn(false);

        page.shouldBeVisible(field);
    }

    @Test(expected = AssertionError.class)
    public void should_be_not_visible_should_throw_an_assertion_if_element_is_visible() {
        BasicPageObject page = new BasicPageObject(driver);
        WebElement field = mock(WebElement.class);
        when(field.isDisplayed()).thenReturn(true);

        page.shouldNotBeVisible(field);
    }

    @Test
    public void should_be_not_visible_should_do_nothing_if_element_is_not_visible() {
        BasicPageObject page = new BasicPageObject(driver);
        WebElement field = mock(WebElement.class);
        when(field.isDisplayed()).thenReturn(false);

        page.shouldNotBeVisible(field);
    }

    @Test
    public void should_be_visible_should_do_nothing_if_element_is_visible() {
        BasicPageObject page = new BasicPageObject(driver);
        WebElement field = mock(WebElement.class);

        when(field.isDisplayed()).thenReturn(true);
        page.shouldBeVisible(field);
    }

    @Test
    public void should_be_visible_should_handle_changing_field_state() {
        BasicPageObject page = new BasicPageObject(driver);
        WebElement field = mock(WebElement.class);

        when(field.isDisplayed()).thenReturn(true);
        page.shouldBeVisible(field);

        when(field.isDisplayed()).thenReturn(false);
        page.shouldNotBeVisible(field);
    }

    @Test(expected = WebDriverException.class)
    public void when_clicking_on_something_should_throw_exception_if_it_fails_twice() {
        BasicPageObject page = new BasicPageObject(driver);
        when(mockButton.isEnabled()).thenReturn(true);
        when(mockButton.getTagName()).thenReturn("button");
        doThrow(new WebDriverException()).when(mockButton).click();

        page.clickOn(page.getButton());
    }

    @Test
    public void page_should_detect_if_a_web_element_contains_a_string() {

        BasicPageObject page = new BasicPageObject(driver);
        WebElement searchedBlock = mock(WebElement.class);
        when(searchedBlock.getText()).thenReturn("red green blue");

        assertThat(page.containsTextInElement(searchedBlock, "red"), is(true));
    }

    @Test
    public void page_should_detect_if_a_web_element_does_not_contain_a_string() {

        BasicPageObject page = new BasicPageObject(driver);
        WebElement searchedBlock = mock(WebElement.class);
        when(searchedBlock.getText()).thenReturn("red green blue");

        assertThat(page.containsTextInElement(searchedBlock, "orange"), is(false));
    }

    @Test(expected = AssertionError.class)
    public void should_contain_text_in_element_should_throw_an_assertion_if_text_is_not_visible() {
        BasicPageObject page = new BasicPageObject(driver);
        WebElement searchedBlock = mock(WebElement.class);
        when(searchedBlock.getText()).thenReturn("red green blue");

        page.shouldContainTextInElement(searchedBlock, "orange");
    }

    @Test
    public void should_contain_text_in_web_element_should_do_nothing_if_text_is_present() {
        BasicPageObject page = new BasicPageObject(driver);
        WebElement searchedBlock = mock(WebElement.class);
        when(searchedBlock.getText()).thenReturn("red green blue");

        page.shouldContainTextInElement(searchedBlock, "red");
    }

    @Test
    public void should_not_contain_text_in_web_element_should_do_nothing_if_text_is_not_present() {
        BasicPageObject page = new BasicPageObject(driver);
        WebElement searchedBlock = mock(WebElement.class);
        when(searchedBlock.getText()).thenReturn("red green blue");

        page.shouldNotContainTextInElement(searchedBlock, "orange");
    }

}
