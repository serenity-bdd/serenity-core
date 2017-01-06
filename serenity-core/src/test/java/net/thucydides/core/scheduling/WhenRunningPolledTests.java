package net.thucydides.core.scheduling;

import net.serenitybdd.core.pages.PageObject;
import net.thucydides.core.steps.ExecutedStepDescription;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.steps.StepFailure;
import net.thucydides.core.util.MockEnvironmentVariables;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.configuration.SystemPropertiesConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Clock;
import org.openqa.selenium.support.ui.Duration;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Sleeper;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class WhenRunningPolledTests {

    @Mock
    WebDriver driver;

    @Mock
    Sleeper sleeper;

    @Mock
    StepFailure failure;

    @Mock
    WebDriver.Navigation navigation;

    MockEnvironmentVariables environmentVariables;

    Configuration configuration;

    class Counter {
        int counter = 0;

        public int getCounter() {
            return counter;
        }

        public void incrementCounter() {
            counter++;
        }
    }

    class ATestClass {
        public void someTest() {}
    }

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        environmentVariables = new MockEnvironmentVariables();
        configuration = new SystemPropertiesConfiguration(environmentVariables);

        when(driver.navigate()).thenReturn(navigation);

        StepEventBus.getEventBus().reset();
        StepEventBus.getEventBus().testSuiteStarted(ATestClass.class);
        StepEventBus.getEventBus().testStarted("someTest");
    }

    class SlowPage extends PageObject {

        public SlowPage(final WebDriver driver) {
            super(driver);
        }
    }


    private ExpectedCondition<Boolean> weHaveWaitedEnough(final Counter counter) {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                counter.incrementCounter();
                return counter.getCounter() > 3;
            }
        };
    }

    @Test
    public void if_requested_page_should_be_refreshed_during_wait() {
        SlowPage page = new SlowPage(driver);
        Counter counter = new Counter();

        page.waitForWithRefresh()
                .withTimeoutOf(5000).milliseconds()
                .pollingEvery(100).milliseconds()
                .until(weHaveWaitedEnough(counter));

        verify(navigation, times(3)).refresh();
    }

    @Test
    public void wait_should_be_bypassed_if_a_previous_step_has_failed() {
        SlowPage page = new SlowPage(driver);
        Counter counter = new Counter();

        StepEventBus.getEventBus().testSuiteStarted(ATestClass.class);
        StepEventBus.getEventBus().testStarted("someTest");
        StepEventBus.getEventBus().stepStarted(ExecutedStepDescription.withTitle("a step"));
        StepEventBus.getEventBus().stepFailed(failure);

        page.waitForWithRefresh()
                .withTimeoutOf(5000).milliseconds()
                .pollingEvery(100).milliseconds()
                .until(weHaveWaitedEnough(counter));

        verify(navigation, times(0)).refresh();
    }

    @Test
    public void normally_page_should_be_not_refreshed_during_wait() {
        SlowPage page = new SlowPage(driver);

        Counter counter = new Counter();
        page.waitForCondition()
                .withTimeoutOf(5000).milliseconds()
                .pollingEvery(100).milliseconds()
                .until(weHaveWaitedEnough(counter));

        verify(navigation, never()).refresh();
    }

    @Test
    public void page_should_pause_during_wait() throws InterruptedException {

        Clock clock = new org.openqa.selenium.support.ui.SystemClock();
        NormalFluentWait<WebDriver> waitFor = new NormalFluentWait(driver, clock, sleeper);
        Counter counter = new Counter();

        waitFor.withTimeoutOf(5000).milliseconds()
                .pollingEvery(100).milliseconds()
                .until(weHaveWaitedEnough(counter));

        verify(sleeper, times(3)).sleep(new Duration(100, TimeUnit.MILLISECONDS));
    }


    private ExpectedCondition<Boolean> weSpitTheDummy(final Counter counter) {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                counter.incrementCounter();
                if (counter.getCounter() > 3) {
                    throw new AssertionError("Oh drat");
                }
                return false;
            }
        };
    }

    private ExpectedCondition<Boolean> weSpitTheDummyWithARuntimeException(final Counter counter) {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                counter.incrementCounter();
                if (counter.getCounter() < 3) {
                    throw new NullPointerException("Oh drat");
                } else {
                    return true;
                }
            }
        };
    }

    private ExpectedCondition<Boolean> weTakeTooLong() {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return false;
            }
        };
    }

    private ExpectedCondition<String> weDefineAnInvalidCondition() {
        return new ExpectedCondition<String>() {
            public String apply(WebDriver driver) {
                return null;
            }
        };
    }

    @Test(expected = AssertionError.class)
    public void should_propogate_exception_if_test_fails() {
        SlowPage page = new SlowPage(driver);
        Counter counter = new Counter();

        page.waitForCondition()
                .withTimeoutOf(5000).milliseconds()
                .pollingEvery(100).milliseconds()
                .until(weSpitTheDummy(counter));

    }

    @Test(expected = NullPointerException.class)
    public void should_propogate_exception_if_test_fails_with_runtime_error() {
        SlowPage page = new SlowPage(driver);
        Counter counter = new Counter();

        page.waitForCondition()
                .withTimeoutOf(5000).milliseconds()
                .pollingEvery(100).milliseconds()
                .until(weSpitTheDummyWithARuntimeException(counter));

    }

    @Test
    public void should_not_propogate_exception_if_test_fails_with_an_ignored_exception() {
        SlowPage page = new SlowPage(driver);
        Counter counter = new Counter();

        page.waitForCondition()
                .ignoring(NullPointerException.class)
                .withTimeoutOf(5000).milliseconds()
                .pollingEvery(100).milliseconds()
                .until(weSpitTheDummyWithARuntimeException(counter));

    }

    @Test(expected = TimeoutException.class)
    public void should_timeout_if_takes_too_long() {
        SlowPage page = new SlowPage(driver);

        page.waitForCondition()
                .withTimeoutOf(1000).milliseconds()
                .pollingEvery(25).milliseconds()
                .until(weTakeTooLong());

    }


    @Test(expected = IllegalArgumentException.class)
    public void should_check_that_condition_is_a_boolean_function() {
        SlowPage page = new SlowPage(driver);

        page.waitForCondition()
                .withTimeoutOf(5000).milliseconds()
                .pollingEvery(25).milliseconds()
                .until(weDefineAnInvalidCondition());

    }


    private ExpectedBackendCondition<BackEnd, Boolean> weHaveWaitedAWhile() {
        return new ExpectedBackendCondition<BackEnd, Boolean>() {
            public Boolean apply(BackEnd backend) {
                return backend.isBackendReady();
            }
        };
    }


    private class BackEnd {
        int counter = 0;

        public boolean isBackendReady() {
            return (counter++ > 5);
        }

        public int getCounter() {
            return counter;
        }
    }

    @Test
    public void should_be_able_to_wait_for_non_web_tests_too() throws InterruptedException {

        BackEnd backEnd = new BackEnd();
        NormalFluentWait<BackEnd> waitFor = new NormalFluentWait(backEnd);

        waitFor.withTimeoutOf(5000).milliseconds()
                .pollingEvery(10).milliseconds()
                .until(weHaveWaitedAWhile());

        assertThat(backEnd.getCounter()).isGreaterThan(5);
    }
}
