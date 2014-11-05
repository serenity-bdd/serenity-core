package net.thucydides.core.steps.integration;

import net.thucydides.core.annotations.Step;
import net.thucydides.core.annotations.StepGroup;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;

import java.io.IOException;

import static net.thucydides.core.steps.StepData.setDefaultStepFactory;
import static net.thucydides.core.steps.StepData.withTestDataFrom;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class WhenStepFailedInStepsWithTestData {

    @Mock
    WebDriver driver;

    @Mock
    Logger logger;

    @Mock
    StepListener listener;

    @Mock
    StepInterceptor stepInterceptor;

    private StepFactory factory;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        factory = new StepFactory(new Pages(driver));

        StepEventBus.getEventBus().clear();
        StepEventBus.getEventBus().registerListener(listener);
        setDefaultStepFactory(null);
    }

    static class TestSteps extends ScenarioSteps {

        private String name;

        public TestSteps(Pages pages) {
            super(pages);
        }

        @StepGroup
        public void step_group_with_fail_step_in_one_iteration() {
            step1(); // should be always done
            fail_step_in_one_iteration(); // should be failed in first iteration
            step2();   // should be skipped in first iteration
            step3();   // should be skipped in first iteration
        }

        @StepGroup
        public void step_group_with_fail_step() {
            step1(); // should be always done
            fail_step(); // should be always failed
            step2();   // should be always skipped
            step3();   // should be always skipped
        }

        @Step
        public void step1() {
        }

        @Step
        public void step2() {
        }

        @Step
        public void step3() {
        }

        @Step
        public void fail_step() {
            throw new AssertionError("Bad name");
        }

        @Step
        public void fail_step_in_one_iteration() {
            if (name.equals("Bill")) {
                throw new AssertionError("Bad name");
            }
        }
    }

    @Test
    public void should_skip_step_after_first_failed_in_one_iteration() throws IOException {
        TestSteps steps = factory.getStepLibraryFor(TestSteps.class);

        setDefaultStepFactory(factory);

        withTestDataFrom("testdata/test.csv").run(steps).step_group_with_fail_step_in_one_iteration();

        verify(listener, times(1)).stepFailed(any(StepFailure.class));
        verify(listener, times(2)).skippedStepStarted(any(ExecutedStepDescription.class));
        verify(listener, times(2)).stepIgnored();
    }

    @Test
    public void should_skip_step_after_first_failed() throws IOException {
        TestSteps steps = factory.getStepLibraryFor(TestSteps.class);

        setDefaultStepFactory(factory);

        withTestDataFrom("testdata/test.csv").run(steps).step_group_with_fail_step();

        verify(listener, times(3)).stepFailed(any(StepFailure.class));
        verify(listener, times(6)).skippedStepStarted(any(ExecutedStepDescription.class));
        verify(listener, times(6)).stepIgnored();
    }
}
