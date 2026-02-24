package net.thucydides.core.steps;

import net.serenitybdd.annotations.Story;
import net.serenitybdd.model.environment.ConfiguredEnvironment;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.samples.ExtendChildScenarioSteps;
import net.thucydides.core.steps.samples.ExtendParentScenarioSteps;
import net.thucydides.core.util.ExtendedTemporaryFolder;
import net.thucydides.model.configuration.SystemPropertiesConfiguration;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.domain.TestResult;
import net.thucydides.model.environment.MockEnvironmentVariables;
import net.thucydides.model.webdriver.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * With this example, I want to show that the @test annotation
 * with a description is not always correctly overridden together with the class method.
 * So identical implementations of get and post in one class worked differently.
 */
public class WhenStepsExtends {

    @Story(storyClass = WhenRecordingStepExecutionResults.MyStory.class)
    class MyTestCase {
        public void app_should_work() {
        }
    }

    @Rule
    public ExtendedTemporaryFolder temporaryFolder = new ExtendedTemporaryFolder();

    @Mock
    Pages pages;

    @Mock
    TestOutcome testOutcome;

    File outputDirectory;

    BaseStepListener stepListener;

    StepFactory stepFactory;

    MockEnvironmentVariables environmentVariables;

    Configuration configuration;

    StepEventBus eventBus;

    @Before
    public void createStepListenerAndFactory() throws IOException {

        eventBus = new StepEventBus(environmentVariables, ConfiguredEnvironment.getConfiguration());

        MockitoAnnotations.initMocks(this);

        outputDirectory = temporaryFolder.newFolder("thucydides");
        stepFactory = StepFactory.getFactory().usingPages(pages);

        environmentVariables = new MockEnvironmentVariables();
        configuration = new SystemPropertiesConfiguration(environmentVariables);

        stepListener = new BaseStepListener(FirefoxDriver.class, outputDirectory, configuration);

        StepEventBus.getParallelEventBus().registerListener(stepListener);
    }

    @After
    public void dropListener() {
        stepListener.testSuiteFinished();
        StepEventBus.getParallelEventBus().dropListener(stepListener);
    }

    @Test
    public void first_the_parent_step_then_the_child_step() {

        StepEventBus.getParallelEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work");

        ExtendChildScenarioSteps childSteps = stepFactory.getSharedStepLibraryFor(ExtendChildScenarioSteps.class);
        ExtendParentScenarioSteps parentSteps = stepFactory.getSharedStepLibraryFor(ExtendParentScenarioSteps.class);
        parentSteps.addChildScenarioStep();
        childSteps.addChildScenarioStep();
        parentSteps.getChildScenarioStep();
        childSteps.getChildScenarioStep();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);
        System.out.println(results);
        System.out.println(testOutcome);
        assertThat(testOutcome.toString(), is("App should work:Add parent scenario step, Add child scenario step, Get parent scenario step, Get child scenario step"));

        assertThat(testOutcome.getTestSteps().get(2).getResult(), is(TestResult.SUCCESS));
    }

    @Test
    public void first_the_child_step_then_the_parent_step() {
        StepEventBus.getParallelEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work");

        ExtendChildScenarioSteps childSteps = stepFactory.getSharedStepLibraryFor(ExtendChildScenarioSteps.class);
        ExtendParentScenarioSteps parentSteps = stepFactory.getSharedStepLibraryFor(ExtendParentScenarioSteps.class);
        childSteps.addChildScenarioStep();
        parentSteps.addChildScenarioStep();
        childSteps.getChildScenarioStep();
        parentSteps.getChildScenarioStep();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);
        System.out.println(results);
        System.out.println(testOutcome);
        assertThat(testOutcome.toString(), is("App should work:Add child scenario step, Add parent scenario step, Get child scenario step, Get parent scenario step"));

        assertThat(testOutcome.getTestSteps().get(2).getResult(), is(TestResult.SUCCESS));
    }
}
