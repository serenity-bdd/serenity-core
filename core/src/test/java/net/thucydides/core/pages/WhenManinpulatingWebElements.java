package net.thucydides.core.pages;

import net.thucydides.core.webdriver.javascript.JavascriptExecutorFacade;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.*;
import org.openqa.selenium.support.pagefactory.ElementLocator;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class WhenManinpulatingWebElements {

    @Mock
    WebDriver driver;

    @Mock
    WebElement webElement;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void stale_element_should_not_be_considered_visible() {
        when(webElement.isDisplayed()).thenThrow(new StaleElementReferenceException("Stale element"));

        WebElementFacade elementFacade = WebElementFacadeImpl.wrapWebElement(driver, webElement, 100);

        assertThat(elementFacade.isVisible(), is(false));

    }

    @Test
    public void web_element_facade_should_be_printed_as_the_web_element() {
        when(webElement.getAttribute("id")).thenReturn("idvalue");
        when(webElement.getTagName()).thenReturn("tag");
        WebElementFacade elementFacade = WebElementFacadeImpl.wrapWebElement(driver, webElement, 100);

        assertThat(elementFacade.toString(), is("<tag id='idvalue'>"));

    }

    @Test
    public void web_element_facade_should_be_printed_using_name_if_id_not_available() {
        when(webElement.getAttribute("name")).thenReturn("somename");
        when(webElement.getTagName()).thenReturn("tag");
        WebElementFacade elementFacade = WebElementFacadeImpl.wrapWebElement(driver, webElement, 100);

        assertThat(elementFacade.toString(), is("<tag name='somename'>"));
    }


    @Test
    public void web_element_facade_should_be_printed_using_class_if_id_and_name_not_available() {
        when(webElement.getAttribute("class")).thenReturn("someclass");
        when(webElement.getTagName()).thenReturn("tag");
        WebElementFacade elementFacade = WebElementFacadeImpl.wrapWebElement(driver, webElement, 100);

        assertThat(elementFacade.toString(), is("<tag class='someclass'>"));
    }

    @Test
    public void web_element_facade_should_be_printed_using_href_if_present() {
        when(webElement.getAttribute("href")).thenReturn("link");
        when(webElement.getTagName()).thenReturn("a");
        WebElementFacade elementFacade = WebElementFacadeImpl.wrapWebElement(driver, webElement, 100);

        assertThat(elementFacade.toString(), is("<a href='link'>"));
    }

    @Test
    public void web_element_facade_should_be_printed_using_type_and_name_if_present() {
        when(webElement.getAttribute("type")).thenReturn("button");
        when(webElement.getAttribute("value")).thenReturn("submit");
        when(webElement.getTagName()).thenReturn("input");
        WebElementFacade elementFacade = WebElementFacadeImpl.wrapWebElement(driver, webElement, 100);

        assertThat(elementFacade.toString(), is("<input type='button' value='submit'>"));
    }
    @Test
    public void web_element_facade_should_be_printed_as_tag_element_if_nothing_available() {

        WebElementFacade elementFacade = WebElementFacadeImpl.wrapWebElement(driver, webElement, 100);
        when(webElement.getTagName()).thenReturn("tag");

        assertThat(elementFacade.toString(), is("<tag>"));
    }


    @Test
    public void stale_element_found_using_a_finder_should_not_be_considered_displayed() {
        when(driver.findElements((By) anyObject())).thenThrow(new StaleElementReferenceException("Stale element"));

        RenderedPageObjectView view = new RenderedPageObjectView(driver, 100);

        assertThat(view.elementIsDisplayed(By.id("some-element")), is(false));

    }


    @Test
    public void inexistant_element_should_not_be_considered_present() {
        when(driver.findElements((By) anyObject())).thenThrow(new NoSuchElementException("It ain't there."));

        RenderedPageObjectView view = new RenderedPageObjectView(driver, 100);

        assertThat(view.elementIsPresent(By.id("some-element")), is(false));

    }

    @Test
    public void an_element_on_the_page_should_be_considered_present() {
        List<WebElement> presentElements = Arrays.asList(webElement);
        when(driver.findElements((By) anyObject())).thenReturn(presentElements);

        RenderedPageObjectView view = new RenderedPageObjectView(driver, 100);

        assertThat(view.elementIsPresent(By.id("some-element")), is(true));
    }

    @Test
    public void timeout_can_be_redefined() {
        WebElementFacade webElementFacade = WebElementFacadeImpl.wrapWebElement(driver, webElement, 100);
        WebElementFacade webElementFacadeWithDifferentTimeout = webElementFacade.withTimeoutOf(2, TimeUnit.SECONDS);

        assertThat(webElementFacadeWithDifferentTimeout.getTimeoutInMilliseconds(), is(2000L));
    }

    @Test
    public void inexistant_element_should_not_be_considered_displayed() {
        when(driver.findElements((By) anyObject())).thenThrow(new NoSuchElementException("It ain't there."));

        RenderedPageObjectView view = new RenderedPageObjectView(driver, 100);

        assertThat(view.elementIsDisplayed(By.id("some-element")), is(false));

    }

    @Test
    public void stale_element_should_not_be_considered_enabled() {
        when(webElement.isDisplayed()).thenThrow(new StaleElementReferenceException("Stale element"));

        WebElementFacade elementFacade = WebElementFacadeImpl.wrapWebElement(driver, webElement, 100);

        assertThat(elementFacade.isCurrentlyEnabled(), is(false));

    }

    @Mock
    JavascriptExecutorFacade mockJavascriptExecutorFacade;

    @Test
    public void element_can_set_window_focus() {
        WebElementFacade elementFacade = new WebElementFacadeImpl(driver, (ElementLocator) null, 100) {
            @Override
            protected JavascriptExecutorFacade getJavascriptExecutorFacade() {
                return mockJavascriptExecutorFacade;
            }
        };
        elementFacade.setWindowFocus();

        verify(mockJavascriptExecutorFacade).executeScript("window.focus()");

    }

    @Test
    public void when_text_attribute_is_null_textvalue_should_return_value() {
        when(webElement.isDisplayed()).thenReturn(true);
        when(webElement.getText()).thenReturn(null);
        when(webElement.getTagName()).thenReturn("input");
        when(webElement.getAttribute("value")).thenReturn("value");

        WebElementFacade elementFacade = WebElementFacadeImpl.wrapWebElement(driver, webElement, 100);

        assertThat(elementFacade.getTextValue(), is("value"));
    }

    @Test
    public void when_text_attribute_and_text_value_are_null_textvalue_should_return_empty_string() {
        when(webElement.isDisplayed()).thenReturn(true);
        when(webElement.getText()).thenReturn(null);
        when(webElement.getTagName()).thenReturn("input");
        when(webElement.getAttribute("value")).thenReturn(null);

        WebElementFacade elementFacade = WebElementFacadeImpl.wrapWebElement(driver, webElement, 100);

        assertThat(elementFacade.getTextValue(), is(""));
    }

    @Test
    public void when_value_is_null_textvalue_should_return_text() {
        when(webElement.isDisplayed()).thenReturn(true);
        when(webElement.getText()).thenReturn("text");
        when(webElement.getTagName()).thenReturn("textarea");
        when(webElement.getAttribute("value")).thenReturn(null);

        WebElementFacade elementFacade = WebElementFacadeImpl.wrapWebElement(driver, webElement, 100);

        assertThat(elementFacade.getTextValue(), is("text"));
    }

    @Test
    public void when_webelement_is_null_it_should_be_considered_invisible() {
        WebElementFacade webElementFacade = WebElementFacadeImpl.wrapWebElement(driver, (WebElement)null, 100);

        assertThat(webElementFacade.isVisible(), is(false));
    }

    @Test
    public void when_webelement_is_null_it_should_be_considered_not_present() {
        WebElementFacade webElementFacade = WebElementFacadeImpl.wrapWebElement(driver, (WebElement)null, 100);

        assertThat(webElementFacade.isPresent(), is(false));
    }

    @Test
    public void when_webelement_is_null_it_should_be_considered_not_enabled() {
        WebElementFacade webElementFacade = WebElementFacadeImpl.wrapWebElement(driver, (WebElement)null, 100);
        assertThat(webElementFacade.isEnabled(), is(false));
    }

    @Test(expected = ElementNotVisibleException.class)
    public void when_webelement_is_null_it_should_not_be_clickable() {
        WebElementFacade webElementFacade = WebElementFacadeImpl.wrapWebElement(driver, (WebElement)null, 100);
        webElementFacade.click();
    }

    @Test(expected = ElementNotVisibleException.class)
    public void when_webelement_is_null_it_should_fail_wait_until_enabled() {
        WebElementFacade webElementFacade = WebElementFacadeImpl.wrapWebElement(driver, (WebElement)null, 100);
        webElementFacade.waitUntilEnabled();
    }

    @Test(expected = ElementNotVisibleException.class)
    public void when_webelement_is_null_it_should_fail_wait_until_disabled() {
        WebElementFacade webElementFacade = WebElementFacadeImpl.wrapWebElement(driver, (WebElement)null, 100);
        webElementFacade.waitUntilDisabled();
    }

    @Test(expected = ElementNotVisibleException.class)
    public void when_webelement_is_null_it_should_fail_wait_until_present() {
        WebElementFacade webElementFacade = WebElementFacadeImpl.wrapWebElement(driver, (WebElement)null, 100);
        webElementFacade.waitUntilPresent();
    }

    @Test
    public void when_webelement_is_null_it_should_succeed_wait_until_not_visible() {
        WebElementFacade webElementFacade = WebElementFacadeImpl.wrapWebElement(driver, (WebElement)null, 100);
        webElementFacade.waitUntilNotVisible();
    }

    @Test
    public void when_webelement_is_null_contains_text_should_fail() {
        WebElementFacade webElementFacade = WebElementFacadeImpl.wrapWebElement(driver, (WebElement)null, 100);
        assertThat(webElementFacade.containsText("text"), is(false));
    }

    @Test
    public void when_webelement_is_null_contains_selected_value_should_fail() {
        WebElementFacade webElementFacade = WebElementFacadeImpl.wrapWebElement(driver, (WebElement)null, 100);
        assertThat(webElementFacade.containsSelectOption("value"), is(false));
    }

    @Test(expected = ElementNotVisibleException.class)
    public void when_webelement_is_null_get_selected_value_should_fail() {
        WebElementFacade webElementFacade = WebElementFacadeImpl.wrapWebElement(driver, (WebElement)null, 100);
        webElementFacade.getSelectedValue();
    }

    @Test(expected = ElementNotVisibleException.class)
    public void when_webelement_is_null_get_text_value_should_fail() {
        WebElementFacade webElementFacade = WebElementFacadeImpl.wrapWebElement(driver, (WebElement)null, 100);
        webElementFacade.getTextValue();
    }

    @Test(expected = ElementNotVisibleException.class)
    public void when_webelement_is_null_get_value_should_fail() {
        WebElementFacade webElementFacade = WebElementFacadeImpl.wrapWebElement(driver, (WebElement)null, 100);
        webElementFacade.getValue();
    }

    @Test(expected = ElementNotVisibleException.class)
    public void when_webelement_is_null_get_text_should_fail() {
        WebElementFacade webElementFacade = WebElementFacadeImpl.wrapWebElement(driver, (WebElement)null, 100);
        webElementFacade.getText();
    }
}
