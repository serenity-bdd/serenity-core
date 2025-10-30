package net.thucydides.core.pages;

import net.serenitybdd.annotations.At;
import net.serenitybdd.core.pages.PageObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WhenMatchingUrlsToPages {

    @Mock
    WebDriver webdriver;
    
    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }
        
    @Test
    public void by_default_all_urls_will_work_with_a_page() {
        PageWithNoUrlDefinitions page = new PageWithNoUrlDefinitions(webdriver);
        assertThat(page.compatibleWithUrl("http://www.apache.org"), is(true));
    }

    @At("http://www.apache.org")
    final class PageObjectWithHardcodedUrlDefinition extends PageObject {
        public PageObjectWithHardcodedUrlDefinition(WebDriver driver) {
            super(driver);
        }
    }
    
    @Test
    public void defining_a_url_will_limit_compatiblity_to_that_url() {
        PageObject page = new PageObjectWithHardcodedUrlDefinition(webdriver);
        assertThat(page.compatibleWithUrl("http://www.apache.org"), is(true));
    }

    @Test
    public void should_ignore_trailing_slashes_in_incoming_url() {
        PageObject page = new PageObjectWithHardcodedUrlDefinition(webdriver);
        assertThat(page.compatibleWithUrl("http://www.apache.org/"), is(true));
    }

    @Test
    public void defining_a_url_will_deny_compatiblity_with_a_different_url() {
        PageObject page = new PageObjectWithHardcodedUrlDefinition(webdriver);
        assertThat(page.compatibleWithUrl("http://projects.apache.org"), is(false));
    }
    
    @At("http://.*.apache.org")
    final class PageObjectForAnyApacheProject extends PageObject {
        public PageObjectForAnyApacheProject(WebDriver driver) {
            super(driver);
        }
    }
    
    @Test
    public void should_allow_urls_with_wildcards() {
        PageObject page = new PageObjectForAnyApacheProject(webdriver);
        assertThat(page.compatibleWithUrl("http://lucene.apache.org"), is(true));
    }

    @At("http://maven.apache.org/.*")
    final class PageObjectForAnyMavenProjectPage extends PageObject {
        public PageObjectForAnyMavenProjectPage(WebDriver driver) {
            super(driver);
        }
    }
    
    @Test
    public void should_allow_urls_with_trailing_wildcards() {
        PageObject page = new PageObjectForAnyMavenProjectPage(webdriver);
        assertThat(page.compatibleWithUrl("http://maven.apache.org/what-is-maven.html"), is(true));
    }

    @At("http://.*/whatever/comes/next")
    final class PageObjectWithWildcardServerName extends PageObject {
        public PageObjectWithWildcardServerName(WebDriver driver) {
            super(driver);
        }
    }

    @Test
    public void should_allow_wildcard_server_names() {
        PageObject page = new PageObjectWithWildcardServerName(webdriver);
        assertThat(page.compatibleWithUrl("http://maven.apache.org/whatever/comes/next"), is(true));
    }

    @At(urls={"http://maven.apache.org","lucene.apache.org"})
    final class MavenOrLucenePage extends PageObject {
        public MavenOrLucenePage(WebDriver driver) {
            super(driver);
        }
    }

    @Test
    public void should_allow_multiple_urls() {
        PageObject page = new PageObjectForAnyApacheProject(webdriver);
        assertThat(page.compatibleWithUrl("http://lucene.apache.org"), is(true));
        assertThat(page.compatibleWithUrl("http://maven.apache.org"), is(true));
    }

    @At(urls={"#HOST/"})
    final class GenericHomePage extends PageObject {
        public GenericHomePage(WebDriver driver) {
            super(driver);
        }
    }

    @Test
    public void should_allow_a_generic_home_page_definition() {
        PageObject page = new GenericHomePage(webdriver);
        assertThat(page.compatibleWithUrl("http://lucene.apache.org/"), is(true));
        assertThat(page.compatibleWithUrl("http://maven.apache.org/something"), is(false));
        assertThat(page.compatibleWithUrl("http://maven.apache.org/something/"), is(false));
    }

    @At(urls={"#HOST/somewhere"})
    final class GenericInternalPage extends PageObject {
        public GenericInternalPage(WebDriver driver) {
            super(driver);
        }
    }

    @Test
    public void should_allow_a_server_independent_page_definition() {
        PageObject page = new GenericInternalPage(webdriver);
        assertThat(page.compatibleWithUrl("http://lucene.apache.org/somewhere"), is(true));
        assertThat(page.compatibleWithUrl("http://maven.apache.org/somewhere"), is(true));
        assertThat(page.compatibleWithUrl("http://maven.apache.org/somethingelse"), is(false));
    }

    @At(urls={"#HOST/app/action/login.form"})
    final class GenericInternalPageWithWildcard extends PageObject {
        public GenericInternalPageWithWildcard(WebDriver driver) {
            super(driver);
        }
    }

    @Test
    public void should_allow_a_server_independent_page_definition_using_wildcards() {
        PageObject page = new GenericInternalPageWithWildcard(webdriver);
        assertThat(page.compatibleWithUrl("http://my.app.org/app/action/login.form?some=parameters"), is(true));
    }

    @Test
    public void should_match_simple_urls_with_parameters() {
        PageObject page = new PageWithUrlDefinitions(webdriver);
        assertThat(page.compatibleWithUrl("https://www.placemyad.com.au/common/microRegistration?a=1"), is(true));
    }

    @Test
    public void should_match_complex_urls_with_parameters() {
        PageObject page = new PageWithUrlDefinitions(webdriver);
        assertThat(page.compatibleWithUrl("https://www.placemyad.com.au/common/microRegistration?continueAction=print&continueNamespace=%2Femployment"), 
                                         is(true));
    }

    @Test
    public void should_know_what_page_we_are_on() {
        WebDriver driver = mock(WebDriver.class);
        when(driver.getCurrentUrl()).thenReturn("https://www.placemyad.com.au/common/microRegistration");
        Pages pages = new Pages(driver);
        assertThat(pages.isCurrentPageAt(PageWithUrlDefinitions.class), is(true));
    }
    
    @Test
    public void should_know_what_page_we_are_not_on() {
        WebDriver driver = mock(WebDriver.class);
        when(driver.getCurrentUrl()).thenReturn("http://maven.apache.org/somethingelse");
        Pages pages = new Pages(driver);
        assertThat(pages.isCurrentPageAt(PageWithUrlDefinitions.class), is(false));
    }
    
    @Test
    public void should_not_throw_exception_if_on_right_page() {
        WebDriver driver = mock(WebDriver.class);
        when(driver.getCurrentUrl()).thenReturn("https://www.placemyad.com.au/common/microRegistration");
        Pages pages = new Pages(driver);
        pages.currentPageAt(PageWithUrlDefinitions.class);
    }

    @Test(expected=WrongPageError.class)
    public void should_throw_exception_if_not_on_right_page() {
        WebDriver driver = mock(WebDriver.class);
        when(driver.getCurrentUrl()).thenReturn("https://www.placemyad.com.au/common/somewhereElse");
        Pages pages = new Pages(driver);
        pages.currentPageAt(PageWithUrlDefinitions.class);
    }
    
    @Test(expected=WrongPageError.class)
    public void should_throw_exception_if_fetching_a_page_of_the_wrong_type() {
        WebDriver driver = mock(WebDriver.class);
        when(driver.getCurrentUrl()).thenReturn("https://www.placemyad.com.au/common/microRegistration");
        Pages pages = new Pages(driver);
        pages.currentPageAt(ApacheHomePage.class);
    }

}
