package net.thucydides.core.pages;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.PageUrls;
import net.serenitybdd.core.pages.UnableToInvokeWhenPageOpensMethods;
import net.thucydides.core.annotations.*;
import net.thucydides.core.util.MockEnvironmentVariables;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.configuration.SystemPropertiesConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;

import java.net.URL;

import static net.serenitybdd.core.pages.PageObject.withParameters;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WhenDefiningPageUrls {

    @Mock
    WebDriver webdriver;

    MockEnvironmentVariables environmentVariables;

    Configuration configuration;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        environmentVariables = new MockEnvironmentVariables();
        configuration = new SystemPropertiesConfiguration(environmentVariables);
    }

    @DefaultUrl("http://www.apache.org")
    final class PageObjectWithFullUrlDefinition extends PageObject {
        public PageObjectWithFullUrlDefinition(WebDriver driver) {
            super(driver);
        }
    }
    
    @DefaultUrl("http://test.myapp.org/somepage")
    final class PageObjectWithFullUrlAndPageDefinition extends PageObject {
        public PageObjectWithFullUrlAndPageDefinition(WebDriver driver) {
            super(driver);
        }
    }

    @DefaultUrl("http://test.myapp.org:9000/somepage")
    final class PageObjectWithFullUrlAndPageAndPortDefinition extends PageObject {
        public PageObjectWithFullUrlAndPageAndPortDefinition(WebDriver driver) {
            super(driver);
        }
    }

    @DefaultUrl("http://test.myapp.org/somepage/")
    final class PageObjectWithAURrlWithATrainingSlash extends PageObject {
        public PageObjectWithAURrlWithATrainingSlash(WebDriver driver) {
            super(driver);
        }
    }
    @Test
    public void the_url_annotation_should_determine_where_the_page_will_open_to() {
        PageObject page = new PageObjectWithFullUrlDefinition(webdriver);
        PageUrls pageUrls = new PageUrls(page, configuration);
        page.setPageUrls(pageUrls);

        page.open();

        verify(webdriver).get("http://www.apache.org");
    }

    @DefaultUrl("http://some.user.application.com/#Showcase")
    final class PageObjectWithHashNotation extends PageObject {
        public PageObjectWithHashNotation(WebDriver driver) {
            super(driver);
        }
    }

    @Test
    public void the_url_notation_should_work_with_hashes() {
        PageObject page = new PageObjectWithHashNotation(webdriver);
        PageUrls pageUrls = new PageUrls(page, configuration);
        page.setPageUrls(pageUrls);
        
        environmentVariables.setProperty("webdriver.base.url","http://my.application.com");
        page.open();

        verify(webdriver).get("http://my.application.com/#Showcase");
    }

    @Test
    public void the_url_should_use_the_annotation_url_if_the_base_url_is_empty() {
        PageObject page = new PageObjectWithHashNotation(webdriver);
        PageUrls pageUrls = new PageUrls(page, configuration);
        page.setPageUrls(pageUrls);

        environmentVariables.setProperty("webdriver.base.url","");
        page.open();

        verify(webdriver).get("http://some.user.application.com/#Showcase");
    }

    @Test
    public void the_url_should_use_the_annotation_url_if_the_base_url_is_not_defined() {
        PageObject page = new PageObjectWithHashNotation(webdriver);
        PageUrls pageUrls = new PageUrls(page, configuration);
        page.setPageUrls(pageUrls);

        System.clearProperty("webdriver.base.url");
        page.open();

        verify(webdriver).get("http://some.user.application.com/#Showcase");
    }


    final class PageObjectWithNoUrlDefinition extends PageObject {
        public PageObjectWithNoUrlDefinition(WebDriver driver) {
            super(driver);
        }
    }

    @Test
    public void the_webdriver_base_url_system_property_should_not_override_pages() {
        PageObject page = new PageObjectWithFullUrlAndPageDefinition(webdriver);
        PageUrls pageUrls = new PageUrls(page, configuration);
        page.setPageUrls(pageUrls);

        environmentVariables.setProperty("webdriver.base.url","http://staging.myapp.org");
        page.open();

        verify(webdriver).get("http://staging.myapp.org/somepage");
    }

    @Test
    public void the_webdriver_base_url_should_conserve_trailing_slashes() {
        PageObject page = new PageObjectWithAURrlWithATrainingSlash(webdriver);
        PageUrls pageUrls = new PageUrls(page, configuration);
        page.setPageUrls(pageUrls);
        page.open();

        verify(webdriver).get("http://test.myapp.org/somepage/");
    }

    @Test
    public void the_webdriver_base_url_system_property_should_include_full_path() {
        PageObject page = new PageObjectWithFullUrlAndPageDefinition(webdriver);
        PageUrls pageUrls = new PageUrls(page, configuration);
        page.setPageUrls(pageUrls);

        environmentVariables.setProperty("webdriver.base.url","http://staging.myapp.org/myapp-staging");
        page.open();

        verify(webdriver).get("http://staging.myapp.org/myapp-staging/somepage");
    }

    @Test
    public void the_webdriver_base_url_system_property_should_override_protocol() {
        PageObject page = new PageObjectWithFullUrlAndPageDefinition(webdriver);
        PageUrls pageUrls = new PageUrls(page, configuration);
        page.setPageUrls(pageUrls);

        environmentVariables.setProperty("webdriver.base.url","https://staging.myapp.org");
        page.open();

        verify(webdriver).get("https://staging.myapp.org/somepage");
    }

    @Test
    public void the_webdriver_base_url_system_property_should_override_ports() {
        PageObject page = new PageObjectWithFullUrlAndPageAndPortDefinition(webdriver);
        PageUrls pageUrls = new PageUrls(page, configuration);
        page.setPageUrls(pageUrls);

        environmentVariables.setProperty("webdriver.base.url","https://staging.myapp.org:8888");
        page.open();

        verify(webdriver).get("https://staging.myapp.org:8888/somepage");
    }

    @Test
    public void the_base_url_is_overrided_by_the_webdriver_base_url_system_property() {
        PageObject page = new PageObjectWithFullUrlDefinition(webdriver);
        PageUrls pageUrls = new PageUrls(page, configuration);
        page.setPageUrls(pageUrls);

        environmentVariables.setProperty("webdriver.base.url","http://www.wikipedia.org");
        page.open();

        verify(webdriver).get("http://www.wikipedia.org");
    }

    @Test
    public void the_base_url_is_overrided_by_the_webdriver_base_url_system_property_even_with_trailing_slashes() {
        PageObject page = new PageObjectWithFullUrlDefinition(webdriver);
        PageUrls pageUrls = new PageUrls(page, configuration);
        page.setPageUrls(pageUrls);

        environmentVariables.setProperty("webdriver.base.url","http://www.wikipedia.org/");
        page.open();

        verify(webdriver).get("http://www.wikipedia.org/");
    }

    @Test
    public void the_base_url_is_overrided_by_the_webdriver_base_url_system_property_even_with_trailing_slashes_and_should_include_full_path() {
        PageObject page = new PageObjectWithFullUrlAndPageDefinition(webdriver);
        PageUrls pageUrls = new PageUrls(page, configuration);
        page.setPageUrls(pageUrls);

        environmentVariables.setProperty("webdriver.base.url","http://www.wikipedia.org/test/");
        page.open();

        verify(webdriver).get("http://www.wikipedia.org/test/somepage");
    }

    @Test
    public void the_base_url_should_be_used_if_no_url_annotation_is_present() {
        PageObject page = new PageObjectWithNoUrlDefinition(webdriver);
        configuration.setDefaultBaseUrl("http://www.google.com");
        PageUrls pageUrls = new PageUrls(page, configuration);
        page.setPageUrls(pageUrls);

        page.open();

        verify(webdriver).get("http://www.google.com");
    }

    @DefaultUrl("http://jira.mycompany.org/issues/{1}")
    final class PageObjectWithParameterizedUrlDefinition extends PageObject {
        public PageObjectWithParameterizedUrlDefinition(WebDriver driver) {
            super(driver);
        }
    }

    @Test
    public void the_url_annotation_should_let_you_define_a_parameterized_url() {
        PageObject page = new PageObjectWithParameterizedUrlDefinition(webdriver);
        PageUrls pageUrls = new PageUrls(page, configuration);
        page.setPageUrls(pageUrls);

        page.open(withParameters("ISSUE-1"));

        verify(webdriver).get("http://jira.mycompany.org/issues/ISSUE-1");
    }

    @DefaultUrl("http://jira.mycompany.org/project/{1}/issues/{2}")
    final class PageObjectWithMultipleUrlParameters extends PageObject {
        public PageObjectWithMultipleUrlParameters(WebDriver driver) {
            super(driver);
        }
    }

    @Test
    public void the_url_annotation_should_let_you_use_a_parameterized_url_with_the_parameter_values_directly() {
        PageObject page = new PageObjectWithMultipleUrlParameters(webdriver);
        PageUrls pageUrls = new PageUrls(page, configuration);
        page.setPageUrls(pageUrls);

        page.open(withParameters("PROJECT-1","ISSUE-1"));

        verify(webdriver).get("http://jira.mycompany.org/project/PROJECT-1/issues/ISSUE-1");
    }

    @Test
    public void the_pages_object_provides_access_to_the_webdriver_instance() {
        PageObject page = new PageObjectWithParameterizedUrlDefinition(webdriver);
        PageUrls pageUrls = new PageUrls(page, configuration);
        page.setPageUrls(pageUrls);

        page.getDriver().get("http://www.google.com");

        verify(webdriver).get("http://www.google.com");
    }

    @DefaultUrl("http://jira.mycompany.org")
    @NamedUrls(
      {
        @NamedUrl(name = "open.issue", url = "http://jira.mycompany.org/issues/{1}")
      }
    )
    final class PageObjectWithNamedParameterizedUrlDefinition extends PageObject {
        public PageObjectWithNamedParameterizedUrlDefinition(WebDriver driver) {
            super(driver);
        }
    }

    @Test
    public void the_url_annotation_should_let_you_define_a_named_parameterized_url() {
        PageObject page = new PageObjectWithNamedParameterizedUrlDefinition(webdriver);
        PageUrls pageUrls = new PageUrls(page, configuration);
        page.setPageUrls(pageUrls);

        page.open("open.issue", withParameters("ISSUE-1"));

        verify(webdriver).get("http://jira.mycompany.org/issues/ISSUE-1");
    }

    @DefaultUrl("/clients")
    final class PageObjectWithRelativeUrlDefinition extends PageObject {
        public PageObjectWithRelativeUrlDefinition(WebDriver driver) {
            super(driver);
        }
    }

    @Test
    public void the_url_annotation_can_be_relative_to_the_base_url() {
        PageObject page = new PageObjectWithRelativeUrlDefinition(webdriver);
        PageUrls pageUrls = new PageUrls(page, configuration);
        page.setPageUrls(pageUrls);

        configuration.setDefaultBaseUrl("http://myapp.mycompany.com");

        page.open();

        verify(webdriver).get("http://myapp.mycompany.com/clients");
    }

    @DefaultUrl("/clients/")
    final class PageObjectWithRelativeUrlDefinitionAndATrailingSlash extends PageObject {
        public PageObjectWithRelativeUrlDefinitionAndATrailingSlash(WebDriver driver) {
            super(driver);
        }
    }

    @Test
    public void the_url_annotation_can_be_relative_to_the_base_url_with_a_trailing_slash() {
        PageObject page = new PageObjectWithRelativeUrlDefinitionAndATrailingSlash(webdriver);
        PageUrls pageUrls = new PageUrls(page, configuration);
        page.setPageUrls(pageUrls);

        configuration.setDefaultBaseUrl("http://myapp.mycompany.com");

        page.open();

        verify(webdriver).get("http://myapp.mycompany.com/clients/");
    }

    @DefaultUrl("http://jira.mycompany.org")
    @NamedUrls(
      {
        @NamedUrl(name = "open.issue", url = "/issues/{1}")
      }
    )
    final class PageObjectWithDefaultUrlAndNamedParameterizedRelativeUrlDefinition extends PageObject {
        public PageObjectWithDefaultUrlAndNamedParameterizedRelativeUrlDefinition(WebDriver driver) {
            super(driver);
        }
    }


    @Test
    public void the_webdriver_base_url_system_property_should_not_override_pages_with_parameters() {
        PageObject page = new PageObjectWithDefaultUrlAndNamedParameterizedRelativeUrlDefinition(webdriver);
        PageUrls pageUrls = new PageUrls(page, configuration);
        page.setPageUrls(pageUrls);

        environmentVariables.setProperty("webdriver.base.url","http://staging.mycompany.org");
        page.open("open.issue", withParameters("ISSUE-1"));

        verify(webdriver).get("http://staging.mycompany.org/issues/ISSUE-1");
    }


    @Test
    public void the_url_annotation_should_let_you_define_a_named_parameterized_url_relative_to_the_default_url() {
        PageObject page = new PageObjectWithDefaultUrlAndNamedParameterizedRelativeUrlDefinition(webdriver);
        PageUrls pageUrls = new PageUrls(page, configuration);
        page.setPageUrls(pageUrls);

        configuration.setDefaultBaseUrl(null);
        page.open("open.issue", withParameters("ISSUE-1"));

        verify(webdriver).get("http://jira.mycompany.org/issues/ISSUE-1");
    }

    @NamedUrls(
      {
        @NamedUrl(name = "open.issue", url = "/issues/{1}")
      }
    )
    final class PageObjectWithNamedParameterizedRelativeUrlDefinition extends PageObject {
        public PageObjectWithNamedParameterizedRelativeUrlDefinition(WebDriver driver) {
            super(driver);
        }
    }

    @Test
    public void the_url_annotation_should_let_you_define_a_relative_named_parameterized_url() {
        PageObject page = new PageObjectWithNamedParameterizedRelativeUrlDefinition(webdriver);
        PageUrls pageUrls = new PageUrls(page, configuration);
        page.setPageUrls(pageUrls);

        configuration.setDefaultBaseUrl("http://myapp.mycompany.com");

        page.open("open.issue", withParameters("ISSUE-1"));

        verify(webdriver).get("http://myapp.mycompany.com/issues/ISSUE-1");
    }

    @NamedUrls(
      {
              @NamedUrl(name = "open.issue", url = "/issues/{1}"),
              @NamedUrl(name = "close.issue", url = "/issues/close/{1}")
      }
    )
    final class PageObjectWithMultipleNamedUrlDefinitions extends PageObject {
        public PageObjectWithMultipleNamedUrlDefinitions(WebDriver driver) {
            super(driver);
        }
    }

    @Test
    public void when_the_default_url_is_defined_as_a_classpath_url_it_uses_an_absolute_path_from_the_classpath() {
        PageWithDefaultUrlOnTheClasspath page = new PageWithDefaultUrlOnTheClasspath(webdriver);
        PageUrls pageUrls = new PageUrls(page, configuration);
        page.setPageUrls(pageUrls);

        URL staticSiteUrl = Thread.currentThread().getContextClassLoader().getResource("static-site/index.html");

        page.open();

        verify(webdriver).get(staticSiteUrl.toString());

    }

    @Test(expected = IllegalStateException.class)
    public void if_a_classpath_url_is_not_found_an_exception_is_thrown() {
        PageWithInvalidDefaultUrlOnTheClasspath page = new PageWithInvalidDefaultUrlOnTheClasspath(webdriver);
        PageUrls pageUrls = new PageUrls(page, configuration);
        page.setPageUrls(pageUrls);

        page.open();

    }
    @Test
    public void the_url_annotation_should_let_you_define_several_named_parameterized_urls() {
        PageObject page = new PageObjectWithMultipleNamedUrlDefinitions(webdriver);
        PageUrls pageUrls = new PageUrls(page, configuration);
        page.setPageUrls(pageUrls);

        configuration.setDefaultBaseUrl("http://myapp.mycompany.com");

        page.open("close.issue", withParameters("ISSUE-1"));

        verify(webdriver).get("http://myapp.mycompany.com/issues/close/ISSUE-1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void the_url_annotation_should_throw_an_exception_if_no_named_url_is_found() {
        PageObject page = new PageObjectWithMultipleNamedUrlDefinitions(webdriver);
        PageUrls pageUrls = new PageUrls(page, configuration);
        page.setPageUrls(pageUrls);

        configuration.setDefaultBaseUrl("http://myapp.mycompany.com");

        page.open("no.such.template", withParameters("ISSUE-1"));
    }

    @Test
    public void when_we_get_a_target_url_a_normal_url_is_left_unprocessed() {
        String url = PageUrls.getUrlFrom("http://www.google.com");

        assertThat(url, is("http://www.google.com"));
    }

    @Test
    public void when_we_get_a_target_url_a_normal_https_url_is_left_unprocessed() {
        String url = PageUrls.getUrlFrom("https://www.google.com");

        assertThat(url, is("https://www.google.com"));
    }


    @Test
    public void when_we_get_a_target_url_a_classpath_url_is_converted_to_a_file_url() {
        String staticSiteUrl = Thread.currentThread().getContextClassLoader()
                                                     .getResource("static-site/index.html").toString();
        String url = PageUrls.getUrlFrom("classpath:static-site/index.html");

        assertThat(url, is(staticSiteUrl));
    }

    @DefaultUrl("http://localhost:8080/somepage")
    final class PageObjectWithOnOpenPageMethod extends PageObject {

        public boolean pageFullyLoaded;

        public PageObjectWithOnOpenPageMethod(WebDriver driver) {
            super(driver);
        }

        @WhenPageOpens
        public void waitTilPageIsFullyLoaded() {
            pageFullyLoaded = true;
        }
    }


    @DefaultUrl("http://localhost:8080/myapp/somepage")
    final class PageObjectWithLongDefaultUrl extends PageObject {

        public boolean pageFullyLoaded;

        public PageObjectWithLongDefaultUrl(WebDriver driver) {
            super(driver);
        }
    }

    @Test
    public void should_override_base_urls() {
        PageObject page = new PageObjectWithLongDefaultUrl(webdriver);
        PageUrls pageUrls = new PageUrls(page, configuration);
        page.setPageUrls(pageUrls);

        environmentVariables.setProperty("webdriver.base.url","http://prod.server");
        page.open();

        verify(webdriver).get("http://prod.server/myapp/somepage");
    }

    @Test
    public void overriding_base_urls_should_allow_for_trailing_slashes() {
        PageObject page = new PageObjectWithLongDefaultUrl(webdriver);
        PageUrls pageUrls = new PageUrls(page, configuration);
        page.setPageUrls(pageUrls);

        environmentVariables.setProperty("webdriver.base.url","http://prod.server/");
        page.open();

        verify(webdriver).get("http://prod.server/myapp/somepage");
    }

    @Test
    public void should_not_override_base_url_if_empty() {
        PageObject page = new PageObjectWithLongDefaultUrl(webdriver);
        PageUrls pageUrls = new PageUrls(page, configuration);
        page.setPageUrls(pageUrls);

        environmentVariables.clearProperty("webdriver.base.url");
        page.open();

        verify(webdriver).get("http://localhost:8080/myapp/somepage");
    }

    @Test
    public void should_not_override_base_url_if_empty_string() {
        PageObject page = new PageObjectWithLongDefaultUrl(webdriver);
        PageUrls pageUrls = new PageUrls(page, configuration);
        page.setPageUrls(pageUrls);

        environmentVariables.setProperty("webdriver.base.url","");
        page.open();

        verify(webdriver).get("http://localhost:8080/myapp/somepage");
    }

    @Test
    public void annotated_OnOpenPage_methods_should_be_called_when_the_page_is_opened() {
        PageObjectWithOnOpenPageMethod page = new PageObjectWithOnOpenPageMethod(webdriver);
        PageUrls pageUrls = new PageUrls(page, configuration);
        page.setPageUrls(pageUrls);

        environmentVariables.setProperty("webdriver.base.url","http://staging.myapp.org");
        page.open();

        verify(webdriver).get("http://staging.myapp.org/somepage");
        assertThat(page.pageFullyLoaded, is(true));
    }

    @DefaultUrl("http://localhost:8080/somepage")
    final class PageObjectWithOnOpenPageMethodWithParameters extends PageObject {

        public boolean pageFullyLoaded;

        public PageObjectWithOnOpenPageMethodWithParameters(WebDriver driver) {
            super(driver);
        }

        @WhenPageOpens
        public void waitTilPageIsFullyLoaded(int value) {
            pageFullyLoaded = true;
        }
    }

    @Test(expected = UnableToInvokeWhenPageOpensMethods.class)
    public void annotated_OnOpenPage_methods_cannot_have_parameters() {
        PageObject page = new PageObjectWithOnOpenPageMethodWithParameters(webdriver);
        PageUrls pageUrls = new PageUrls(page, configuration);
        page.setPageUrls(pageUrls);

        environmentVariables.setProperty("webdriver.base.url","http://staging.myapp.org");
        page.open();
    }

    @DefaultUrl("http://localhost:8080/somepage")
    final class PageObjectWithFailingOnOpenPageMethod extends PageObject {

        public PageObjectWithFailingOnOpenPageMethod(WebDriver driver) {
            super(driver);
        }

        @WhenPageOpens
        public void checkPageOpens() {
            throw new AssertionError("could not open page properly");
        }
    }

    @Test(expected = AssertionError.class)
    public void annotated_OnOpenPage_methods_should_report_assertion_errors() {
        PageObject page = new PageObjectWithFailingOnOpenPageMethod(webdriver);
        page.open();
    }

    @DefaultUrl("http://localhost:8080/somepage")
    final class PageObjectWithPrivateOnOpenPageMethod extends PageObject {

        public boolean pageFullyLoaded;

        public PageObjectWithPrivateOnOpenPageMethod(WebDriver driver) {
            super(driver);
        }

        @WhenPageOpens
        private void waitTilPageIsFullyLoaded() {
            pageFullyLoaded = true;
        }
    }

    @Test
    public void annotated_OnOpenPage_methods_can_be_private() {
        PageObjectWithPrivateOnOpenPageMethod page = new PageObjectWithPrivateOnOpenPageMethod(webdriver);
        PageUrls pageUrls = new PageUrls(page, configuration);
        page.setPageUrls(pageUrls);

        environmentVariables.setProperty("webdriver.base.url","http://staging.myapp.org");
        page.open();

        verify(webdriver).get("http://staging.myapp.org/somepage");
        assertThat(page.pageFullyLoaded, is(true));
    }

    
    @DefaultUrl("/clients")
    @At(".*/clients")
    final class PageObjectWithRelativeUrlAndPattern extends PageObject {
        public PageObjectWithRelativeUrlAndPattern(WebDriver driver) {
            super(driver);
        }
    }
    
    @Test(expected = WrongPageError.class)
    public void open_should_fail_on_mismatched_url() {
        when(webdriver.getCurrentUrl()).thenReturn("http://sso.mycompany.com/login");
        
        PageObject page = new PageObjectWithRelativeUrlAndPattern(webdriver);
        PageUrls pageUrls = new PageUrls(page, configuration);
        page.setPageUrls(pageUrls);
        configuration.setDefaultBaseUrl("http://myapp.mycompany.com");

        page.open();
    }

    @Test
    public void open_unchecked_should_not_fail_on_mismatched_url() {
        when(webdriver.getCurrentUrl()).thenReturn("http://sso.mycompany.com/login");
        
        PageObject page = new PageObjectWithRelativeUrlAndPattern(webdriver);
        PageUrls pageUrls = new PageUrls(page, configuration);
        page.setPageUrls(pageUrls);
        configuration.setDefaultBaseUrl("http://myapp.mycompany.com");

        page.openUnchecked();

        verify(webdriver).get("http://myapp.mycompany.com/clients");
    }
}
