package net.serenitybdd.core.pages;


import net.thucydides.core.pages.ApacheHomePage;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.pages.WrongPageError;
import net.thucydides.core.util.MockEnvironmentVariables;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.configuration.SystemPropertiesConfiguration;
import net.thucydides.core.webdriver.WebDriverFacade;
import net.thucydides.core.webdriver.WebdriverProxyFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

public class WhenKeepingTrackOfVisitedPages {

    @Mock
    WebDriver driver;
    
    @Mock
    WebDriverFacade driverProxy;

    @Mock
    WebdriverProxyFactory proxyFactory;

    MockEnvironmentVariables environmentVariables;

    Configuration configuration;
    
    @Before
    public void initMocksAndClearSystemwideDefaultUrl() {
        MockitoAnnotations.initMocks(this);
        environmentVariables = new MockEnvironmentVariables();
        configuration = new SystemPropertiesConfiguration(environmentVariables);
    }

    public static class SimplePage extends PageObject {

        public SimplePage(final WebDriver driver) {
            super(driver);
        }
    }

    @Captor
    ArgumentCaptor<String> driverUrl;

    @Test
    public void the_default_starting_point_url_can_refer_to_a_file_on_the_classpath() {

        final String baseUrl = "classpath:static-site/index.html";
        final Pages pages = new Pages(driver, configuration);
        pages.setDefaultBaseUrl(baseUrl);

        pages.get(SimplePage.class).open();

        verify(driver).get(driverUrl.capture());
        assertThat(driverUrl.getValue(), containsString("static-site/index.html"));
    }

    @Test
    public void the_default_starting_point_url_can_be_overriden_by_a_system_property() {

        final String systemDefinedBaseUrl = "http://www.google.com.au";

        environmentVariables.setProperty("webdriver.base.url", systemDefinedBaseUrl);

        Configuration Configuration = new SystemPropertiesConfiguration(environmentVariables);
        final Pages pages = new Pages(driver, Configuration);
        pages.get(SimplePage.class).open();

        verify(driver).get(driverUrl.capture());
        assertThat(driverUrl.getValue(), containsString(systemDefinedBaseUrl));
    }

    @Test
    public void the_pages_object_knows_when_we_are_on_the_right_page() {

        when(driver.getCurrentUrl()).thenReturn("http://www.apache.org");
        final Pages pages = new Pages(driver, configuration);
        assertThat(pages.isCurrentPageAt(ApacheHomePage.class), is(true));
    }

    @Test
    public void the_pages_object_knows_when_we_are_not_on_the_right_page() {

        when(driver.getCurrentUrl()).thenReturn("http://www.google.org");
        final Pages pages = new Pages(driver, configuration);
        assertThat(pages.isCurrentPageAt(ApacheHomePage.class), is(false));
    }

    @Test
    public void the_get_method_is_shorthand_for_currentPageAt() {

        when(driver.getCurrentUrl()).thenReturn("http://www.apache.org");
        final Pages pages = new Pages(driver, configuration);

        assertThat(pages.get(ApacheHomePage.class).getClass().getName(),
                    is(ApacheHomePage.class.getName()));
    }

    @Test
    public void the_getAt_method_is_Groovy_shorthand_for_currentPageAt() {

        when(driver.getCurrentUrl()).thenReturn("http://www.apache.org");
        final Pages pages = new Pages(driver, configuration);

        assertThat(pages.getAt(ApacheHomePage.class).getClass().getName(),
                    is(ApacheHomePage.class.getName()));
    }

    @Test
    public void should_be_able_to_retrieve_the_current_page_as_an_instance_of_AnyPage() {

        when(driver.getCurrentUrl()).thenReturn("http://www.apache.org");
        final Pages pages = new Pages(driver, configuration);

        AnyPage currentPage = pages.getAt(AnyPage.class);
        assertThat(currentPage, is(not(nullValue())));
    }

    @Test(expected = WrongPageError.class)
    public void the_pages_object_throws_a_wrong_page_error_when_we_expect_the_wrong_page() {

        when(driver.getCurrentUrl()).thenReturn("http://www.google.com");
        final Pages pages = new Pages(driver, configuration);

        pages.currentPageAt(ApacheHomePage.class);
    }


    public final class InvalidHomePage extends PageObject {
        public InvalidHomePage() {
            super(null);
        }
    }

    @Test(expected = WrongPageError.class)
    public void the_pages_object_throws_a_wrong_page_error_when_the_page_object_is_invalid() {

        when(driver.getCurrentUrl()).thenReturn("http://www.google.com");
        final Pages pages = new Pages(driver, configuration);

        pages.currentPageAt(InvalidHomePage.class);
    }

    public final class ExplodingHomePage extends PageObject {
        public ExplodingHomePage(final WebDriver driver) throws InstantiationException {
            super(null);
            throw new InstantiationException();
        }
    }

    @Test(expected = WrongPageError.class)
    public void the_pages_object_throws_a_wrong_page_error_when_the_page_object_cant_be_instantiated() {

        when(driver.getCurrentUrl()).thenReturn("http://www.google.com");
        final Pages pages = new Pages(driver, configuration);
        pages.currentPageAt(ExplodingHomePage.class);
    }

    public class PageObjectWithNoDriverConstructor extends PageObject {

        public PageObjectWithNoDriverConstructor() {
            super(null);
        }
    }

    @Test(expected = WrongPageError.class)
    public void the_pages_object_throws_a_wrong_page_error_when_the_page_object_doesnt_have_a_webdriver_constructor() {

        when(driver.getCurrentUrl()).thenReturn("http://www.google.com");
        final Pages pages = new Pages(driver, configuration);
        pages.currentPageAt(PageObjectWithNoDriverConstructor.class);
    }

    public static final class GooglePage extends PageObject {

        public GooglePage(final WebDriver driver) {
            super(driver);
        }
    }

    public static final class SomeOtherPage extends PageObject {

        public SomeOtherPage(final WebDriver driver) {
            super(driver);
        }
    }

    @Test
    public void should_requery_driver_for_each_page_request() {
        when(driver.getCurrentUrl()).thenReturn("http://www.google.com");
        Pages pages = new Pages(driver, configuration);
        pages.setDefaultBaseUrl("http://www.google.com");

        GooglePage page1 = pages.get(GooglePage.class);
        GooglePage page2 = pages.get(GooglePage.class);
        assertThat(page2, is(not(page1)));
    }

    @Test
    public void should_use_the_same_page_object_if_we_indicate_that_are_on_the_same_unchanged_page() {
        when(driver.getCurrentUrl()).thenReturn("http://www.google.com");
        Pages pages = new Pages(driver, configuration);
        pages.setDefaultBaseUrl("http://www.google.com");

        GooglePage page1 = pages.get(GooglePage.class);
        pages.onSamePage();
        GooglePage page2 = pages.get(GooglePage.class);
        assertThat(page2, is(page1));
    }

    @Test
    public void should_use_a_new_page_object_if_we_indicate_that_are_on_the_same_unchanged_page_but_we_are_not() {
        when(driver.getCurrentUrl()).thenReturn("http://www.google.com");
        Pages pages = new Pages(driver, configuration);
        pages.setDefaultBaseUrl("http://www.google.com");

        GooglePage page1 = pages.get(GooglePage.class);
        pages.get(SomeOtherPage.class);
        pages.onSamePage();
        GooglePage page2 = pages.get(GooglePage.class);
        assertThat(page2, is(not(page1)));
    }

    @Test
    public void should_not_open_initial_page_when_driver_opens_if_using_a_proxied_driver() {
        Pages pages = new Pages(driverProxy);
        pages.setDefaultBaseUrl("http://www.google.com");

        verify(driver, never()).get("http://www.google.com");
    }
}
