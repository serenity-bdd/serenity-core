package net.thucydides.core.steps;

import net.serenitybdd.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.model.steps.ExecutedStepDescription;
import net.thucydides.model.steps.StepFailure;
import net.thucydides.model.steps.StepListener;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;

import static net.thucydides.core.steps.stepdata.StepData.setDefaultStepFactory;
import static net.thucydides.core.steps.stepdata.StepData.withTestDataFrom;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class WhenStepFailedInStepsWithTestData {

    @Mock
    WebDriver driver;

    @Mock
    WebDriver.TargetLocator targetLocator;

    @Mock
    Logger logger;

    @Mock
    StepListener listener;

    @Mock
    StepInterceptor stepInterceptor;

    private StepFactory factory;

    @Before
    public void initMocks() throws IOException {
        MockitoAnnotations.initMocks(this);
        factory = StepFactory.getFactory().usingPages(new Pages(driver));

        StepEventBus.getParallelEventBus().reset();
        StepEventBus.getParallelEventBus().registerListener(new BaseStepListener(Files.createTempDirectory("out").toFile()));
        StepEventBus.getParallelEventBus().registerListener(listener);
        setDefaultStepFactory(null);
        when(driver.switchTo()).thenReturn(targetLocator);
    }

    static class TestSteps extends ScenarioSteps {

        private String name;

        public TestSteps(Pages pages) {
            super(pages);
        }

        @Step
        public void step_group_with_fail_step_in_one_iteration() {
            step1(); // should be always done
            fail_step_in_one_iteration(); // should be failed in first iteration
            step2();   // should be skipped in first iteration
            step3();   // should be skipped in first iteration
        }

        @Step
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
}
