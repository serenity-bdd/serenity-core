package net.thucydides.junit.runners.integration;

import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.webdriver.*;
import net.thucydides.junit.rules.QuietThucydidesLoggingRule;
import net.thucydides.junit.rules.SaveWebdriverSystemPropertiesRule;
import net.thucydides.junit.runners.AbstractTestStepRunnerTest;
import net.thucydides.junit.runners.ThucydidesRunner;
import net.thucydides.samples.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Managing the WebDriver instance during a test run
 * The instance should be created once at the start of the test run,
 * and closed once at the end of the tests.
 * 
 * @author johnsmart
 * 
 */
public class WhenManagingAWebDriverInstance extends AbstractTestStepRunnerTest {

    @Rule
    public MethodRule saveSystemProperties = new SaveWebdriverSystemPropertiesRule();


    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    WebdriverInstanceFactory webdriverInstanceFactory;

    @Mock
    FirefoxDriver firefoxDriver;

    @Rule
    public QuietThucydidesLoggingRule quietThucydidesLoggingRule = new QuietThucydidesLoggingRule();

    WebDriverFactory webDriverFactory;

    @Before
    public void createATestableDriverFactory() throws Exception {
        MockitoAnnotations.initMocks(this);

        webdriverInstanceFactory = new WebdriverInstanceFactory() {

            @Override
            public WebDriver newFirefoxDriver(Capabilities profile) {
                return firefoxDriver;
            }

        };

        webDriverFactory = new WebDriverFactory(webdriverInstanceFactory, environmentVariables);

        StepEventBus.getEventBus().clear();

    }


    @Test
    public void the_driver_should_be_initialized_before_the_tests() throws InitializationError  {

        ThucydidesRunner runner = new ThucydidesRunner(SamplePassingScenario.class, webDriverFactory);

        runner.run(new RunNotifier());

        assertThat(firefoxDriver, is(notNullValue()));
    }

    @Mock
    ThucydidesWebdriverManager manager;

    @Test
    public void the_driver_should_be_reset_after_each_test() throws InitializationError {

        ThucydidesRunner runner = new ThucydidesRunner(MultipleTestScenario.class, webDriverFactory);

        runner.run(new RunNotifier());

        verify(firefoxDriver,times(3)).quit();
    }

    @Test
    public void the_driver_should_only_be_reset_once_at_the_start_for_unique_session_tests() throws InitializationError {

        ThucydidesRunner runner = new ThucydidesRunner(MultipleTestScenarioWithUniqueSession.class, webDriverFactory);

        runner.run(new RunNotifier());

        verify(firefoxDriver,times(1)).quit();
    }


    @Test
    public void the_driver_should_be_quit_after_the_tests() throws InitializationError {

        ThucydidesRunner runner = new ThucydidesRunner(SingleTestScenario.class, webDriverFactory);
        
        runner.run(new RunNotifier());
        verify(firefoxDriver).quit();
    }

    @Test
    public void when_an_unsupported_driver_is_used_an_error_is_raised() throws InitializationError {

        environmentVariables.setProperty("webdriver.driver", "netscape");
        try {
            ThucydidesRunner runner = getTestRunnerUsing(SingleTestScenario.class);
            runner.run(new RunNotifier());
            fail();
        } catch (UnsupportedDriverException e) {
            assertThat(e.getMessage(), containsString("Unsupported browser type: netscape"));
        }
    }

    @Test
    public void a_system_provided_url_should_override_the_default_url() throws InitializationError {

        environmentVariables.setProperty("webdriver.base.url", "http://www.wikipedia.com");
        ThucydidesRunner runner = getTestRunnerUsing(SingleWikipediaTestScenario.class);

        runner.run(new RunNotifier());

        verify(firefoxDriver).get("http://www.wikipedia.com");
    }

    @Override
    protected ThucydidesRunner getTestRunnerUsing(Class<?> testClass) throws InitializationError {
        Configuration configuration = new SystemPropertiesConfiguration(environmentVariables);
        return new ThucydidesRunner(testClass, webDriverFactory, configuration);
    }

}
