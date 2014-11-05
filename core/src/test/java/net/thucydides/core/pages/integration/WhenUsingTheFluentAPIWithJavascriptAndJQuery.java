package net.thucydides.core.pages.integration;


import net.thucydides.core.webdriver.javascript.JavascriptExecutorFacade;
import net.thucydides.core.webdriver.jquery.ByJQuery;
import net.thucydides.core.webdriver.jquery.ByJQuerySelector;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

public class WhenUsingTheFluentAPIWithJavascriptAndJQuery {

    private static StaticSitePage page;
    private static WebDriver driver;

    @BeforeClass
    public static void openDriver() {
        driver = new PhantomJSDriver();
        page = new StaticSitePage(driver, 1000);
        page.open();
    }

    @AfterClass
    public static void shutdownDriver() {
        driver.quit();
    }

    @Test
    public void should_inject_jquery_into_the_page() {
        WebDriver driver = new PhantomJSDriver();
        StaticSitePage page = new StaticSitePage(driver, 1000);
        getPage().open();

        getPage().evaluateJavascript("$('#firstname').focus();");

        Boolean jqueryInjected = (Boolean) getPage().evaluateJavascript("return (typeof jQuery === 'function')");
        assertThat(jqueryInjected, is(true));

        driver.quit();
    }

    @Test
    public void should_be_able_to_use_the_javascript_executor_with_parameters() {
        getPage().evaluateJavascript("$('#firstname').focus();", "#firstname");

        assertThat(getPage().element(getPage().firstName).hasFocus(), is(true));
    }

    @Test
    public void should_be_able_to_set_focus_directly() {
        JavascriptExecutorFacade js = new JavascriptExecutorFacade(getPage().getDriver());
        js.executeScript("$('#firstname').focus();");

        assertThat(getPage().element(getPage().firstName).hasFocus(), is(true));
    }

    @Test
    public void should_support_jquery_queries_in_the_page() {

        StaticSitePage page = getPage();
        getPage().evaluateJavascript("$('#firstname').focus();");

        assertThat(getPage().element(getPage().firstName).hasFocus(), is(true));

        getPage().evaluateJavascript("$('#lastname').focus();");

        assertThat(getPage().element(getPage().lastName).hasFocus(), is(true));
    }

    @Test
    public void should_support_jquery_queries_that_return_values_in_the_page() {

        Object result = getPage().evaluateJavascript("return $('#country').val();");

        assertThat(result.toString(), is("Australia"));
    }

    @Test
    public void should_be_able_to_find_an_element_using_a_jquery_expression() {
        WebElement link = getPage().getDriver().findElement(ByJQuery.selector("a[title='Click Me']"));
        assertThat(link.isDisplayed(), is(true));
    }

    @Test
    public void should_be_able_to_find_multiple_elements_using_a_jquery_expression() {
        List<WebElement> links = getPage().getDriver().findElements(ByJQuery.selector("h2"));
        assertThat(links.size(), is(2));
    }

    @Test(expected = WebDriverException.class)
    public void should_fail_gracefully_if_no_jquery_element_is_found() {
        getPage().getDriver().findElement(ByJQuery.selector("a[title='Does Not Exist']"));
    }

    @Test(expected = WebDriverException.class)
    public void should_fail_gracefully_if_jquery_selector_is_invalid() {
        getPage().getDriver().findElement(ByJQuery.selector("a[title='Does Not Exist'"));
    }

    @Test
    public void should_evaluate_javascript_within_browser() {
        String result = (String) getPage().evaluateJavascript("return document.title");
        assertThat(result, is("Thucydides Test Site"));
    }

    @Test
    public void should_execute_javascript_within_browser() {
        getPage().open();
        assertThat(getPage().element(getPage().firstName).hasFocus(), is(false));
        getPage().evaluateJavascript("document.getElementById('firstname').focus()");
        assertThat(getPage().element(getPage().firstName).hasFocus(), is(true));
    }


    @Test
    public void a_jquery_selector_should_be_described_by_the_corresponding_jquery_expression() {
        ByJQuerySelector jQuerySelector = ByJQuery.selector("a[title='Click Me']");

        assertThat(jQuerySelector.toString(), containsString("a[title='Click Me']"));
    }

    public StaticSitePage getPage() {
        try {
            page.getTitle();
        } catch (UnreachableBrowserException e) {
            driver = new PhantomJSDriver();
            page = new StaticSitePage(driver, 1000);            
        }
        return page;
    }
}
