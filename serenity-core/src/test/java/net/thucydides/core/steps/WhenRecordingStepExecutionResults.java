package net.thucydides.core.steps;

import net.serenitybdd.annotations.Feature;
import net.serenitybdd.annotations.Story;
import net.serenitybdd.model.collect.NewList;
import net.serenitybdd.model.environment.ConfiguredEnvironment;
import net.thucydides.core.ListenerInWrongPackage;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.samples.FlatScenarioSteps;
import net.thucydides.core.steps.samples.NestedScenarioSteps;
import net.thucydides.core.steps.samples.StepsDerivedFromADifferentDomain;
import net.thucydides.core.util.ExtendedTemporaryFolder;
import net.thucydides.core.webdriver.ThucydidesWebDriverSupport;
import net.thucydides.model.ThucydidesSystemProperty;
import net.thucydides.model.configuration.SystemPropertiesConfiguration;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.domain.TestResult;
import net.thucydides.model.domain.TestStep;
import net.thucydides.model.domain.TestTag;
import net.thucydides.model.domain.features.ApplicationFeature;
import net.thucydides.model.environment.MockEnvironmentVariables;
import net.thucydides.model.screenshots.ScreenshotException;
import net.thucydides.model.steps.ExecutedStepDescription;
import net.thucydides.model.steps.StepListener;
import net.thucydides.model.util.FileSystemUtils;
import net.thucydides.model.webdriver.Configuration;
import org.junit.*;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.firefox.FirefoxDriver;
import sample.listeners.SampleStepListener;
import some.other.place.StepsInSomeOtherPlace;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
/**
 * We record step execution results using a StepListener.
 * The BaseStepListener implementation provides most of the basic functionality
 * for recording and structuring step results.
 */
public class WhenRecordingStepExecutionResults {

    BaseStepListener stepListener;

    StepFactory stepFactory;

    @Rule
    public ExtendedTemporaryFolder temporaryFolder = new ExtendedTemporaryFolder();

    File outputDirectory;

    byte[] screenshot1;
    byte[] screenshot2;

    @Mock
    FirefoxDriver driver;

    @Mock
    Pages pages;

    @Mock
    TestOutcome testOutcome;

    class AStory {
    }

    class AnotherStory {
    }

    @Story(AStory.class)
    class ATestCase {
        public void app_should_work() {
        }

        public void app_should_still_work() {
        }
    }

    class AStepLibrary extends ScenarioSteps {
        AStepLibrary(Pages pages) {
            super(pages);
        }
    }

    MockEnvironmentVariables environmentVariables;

    Configuration configuration;

    StepEventBus eventBus;
    
    @Before
    public void createStepListenerAndFactory() throws IOException {

        eventBus = new StepEventBus(environmentVariables, ConfiguredEnvironment.getConfiguration());

        MockitoAnnotations.initMocks(this);
        
        outputDirectory = temporaryFolder.newFolder("thucydides");
        File screenshot1File = FileSystemUtils.getResourceAsFile("screenshots/google_page_1.png");
        File screenshot2File = FileSystemUtils.getResourceAsFile("screenshots/google_page_2.png");

        screenshot1 = Files.readAllBytes(screenshot1File.toPath());
        screenshot2 = Files.readAllBytes(screenshot2File.toPath());
        stepFactory = StepFactory.getFactory().usingPages(pages);

        environmentVariables = new MockEnvironmentVariables();
        configuration = new SystemPropertiesConfiguration(environmentVariables);

        when(driver.getCurrentUrl()).thenReturn("http://www.google.com");
        when(driver.toString()).thenReturn("firefox");
        when(driver.getScreenshotAs(any(OutputType.class))).thenReturn(screenshot1).thenReturn(screenshot2);

        ThucydidesWebDriverSupport.closeAllDrivers();
        ThucydidesWebDriverSupport.initialize(driver.toString());
        ThucydidesWebDriverSupport.useDriver(driver);
        assertThat(ThucydidesWebDriverSupport.getCurrentDriverName(),is(driver.toString()));

        stepListener = new BaseStepListener(FirefoxDriver.class, outputDirectory, configuration);

        StepEventBus.getParallelEventBus().registerListener(stepListener);
    }

    @After
    public void dropListener() {
        stepListener.testSuiteFinished();
        StepEventBus.getParallelEventBus().dropListener(stepListener);
    }


    class MyStory {
    }

    class MyOtherStory {
    }

    @Story(MyStory.class)
    class MyTestCase {
        public void app_should_work() {
        }
    }

    @Story(MyOtherStory.class)
    class MyOtherTestCase {
        public void app_should_work() {
        }
    }

    class MyTestCaseWithoutAStory {
        public void app_should_work() {
        }
    }

    @Test
    public void the_listener_can_create_a_new_driver_if_the_pages_factory_driver_is_not_defined() {

        when(pages.getDriver()).thenReturn(null);

        BaseStepListener listener = new BaseStepListener(outputDirectory);

        assertThat(listener.getDriver(), is(notNullValue()));
    }

    @Test
    public void the_listener_can_create_a_new_driver_if_the_pages_factory_is_not_defined() {

        BaseStepListener listener = new BaseStepListener(outputDirectory);

        assertThat(listener.getDriver(), is(notNullValue()));
    }

    @Test
    public void the_listener_should_record_basic_step_execution() {

        StepEventBus.getParallelEventBus().testStarted("app_should_work", MyTestCase.class);

        FlatScenarioSteps steps = stepFactory.getSharedStepLibraryFor(FlatScenarioSteps.class);

        steps.step_one();
        steps.step_two();

        StepEventBus.getParallelEventBus().testFinished();

        List<TestOutcome> results = stepListener.getTestOutcomes();
        assertThat(results.size(), is(1));
        assertThat(results.get(0).toString(), is("App should work:Step one, Step two"));
    }

    @Test
    public void the_listener_should_record_given_when_then_step_execution() {

        StepEventBus.getParallelEventBus().testStarted("app_should_work", MyTestCase.class);

        FlatScenarioSteps steps = stepFactory.getSharedStepLibraryFor(FlatScenarioSteps.class);

        steps.given_some_state();
        steps.when_we_do_something();
        steps.then_this_should_happen();

        StepEventBus.getParallelEventBus().testFinished();

        List<TestOutcome> results = stepListener.getTestOutcomes();
        assertThat(results.size(), is(1));
        assertThat(results.get(0).toString(), is("App should work:Given some state, When we do something, Then this should happen"));
    }

    @Test
    public void the_listener_should_record_issue_tags() {

        StepEventBus.getParallelEventBus().testStarted("app_should_work", MyTestCase.class);

        FlatScenarioSteps steps = stepFactory.getSharedStepLibraryFor(FlatScenarioSteps.class);

        steps.step_one();
        steps.step_two();

        StepEventBus.getParallelEventBus().addIssuesToCurrentTest(NewList.of("issue-123","issue-456"));
        StepEventBus.getParallelEventBus().testFinished();

        List<TestOutcome> results = stepListener.getTestOutcomes();
        assertThat(results.size(), is(1));
        assertThat(results.get(0).getIssueKeys(), hasItems("issue-123","issue-456"));
    }

    @Test
    public void the_listener_should_record_issue_tags_for_multiple_scenarios_in_a_story() {

        FlatScenarioSteps steps = stepFactory.getSharedStepLibraryFor(FlatScenarioSteps.class);

        StepEventBus.getParallelEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getParallelEventBus().addIssuesToCurrentStory(NewList.of("issue-123"));

        StepEventBus.getParallelEventBus().testStarted("app_should_work", MyTestCase.class);
        StepEventBus.getParallelEventBus().addIssuesToCurrentTest(NewList.of("issue-456"));
        StepEventBus.getParallelEventBus().addTagsToCurrentStory(NewList.of(TestTag.withName("iteration-1").andType("iteration")));
        StepEventBus.getParallelEventBus().addTagsToCurrentTest(NewList.of(TestTag.withName("fast").andType("speed")));

        steps.step_one();
        steps.step_two();

        StepEventBus.getParallelEventBus().testFinished();

        StepEventBus.getParallelEventBus().testStarted("app_should_work_again", MyTestCase.class);
        StepEventBus.getParallelEventBus().addIssuesToCurrentTest(NewList.of("issue-789"));

        steps.step_one();
        steps.step_two();

        StepEventBus.getParallelEventBus().testFinished();
        StepEventBus.getParallelEventBus().testSuiteFinished();

        List<TestOutcome> results = stepListener.getTestOutcomes();
        assertThat(results.size(), is(2));
        assertThat(results.get(0).getIssueKeys(), hasItems("issue-123", "issue-456"));
        assertThat(results.get(0).getTags(), hasItem(TestTag.withName("iteration-1").andType("iteration")));
        assertThat(results.get(0).getTags(), hasItem(TestTag.withName("fast").andType("speed")));
        assertThat(results.get(1).getIssueKeys(), hasItems("issue-123","issue-789"));
    }

    @Test
    public void the_listener_should_be_able_to_update_step_names() {

        FlatScenarioSteps steps = stepFactory.getSharedStepLibraryFor(FlatScenarioSteps.class);

        StepEventBus.getParallelEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work", MyTestCase.class);
        steps.step_one();
        steps.step_two();
        StepEventBus.getParallelEventBus().updateCurrentStepTitle("new_step_name");
        StepEventBus.getParallelEventBus().testFinished();
        StepEventBus.getParallelEventBus().testSuiteFinished();

        List<TestOutcome> results = stepListener.getTestOutcomes();
        assertThat(results.size(), is(1));
        assertThat(results.get(0).getTestSteps().get(2).getDescription(), is("new_step_name"));
    }

    @Test
    public void the_listener_should_record_the_tested_story() {

        StepEventBus.getParallelEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work", MyTestCase.class);

        FlatScenarioSteps steps = stepFactory.getSharedStepLibraryFor(FlatScenarioSteps.class);

        steps.step_one();
        steps.step_two();

        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        TestOutcome outcome = stepListener.getTestOutcomes().get(0);
        assertThat(outcome.getUserStory().getName(), is("MyStory"));
    }

    @Test
    public void the_listener_should_record_multiple_tests_and_stories() {

        StepEventBus.getParallelEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work", MyTestCase.class);
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        StepEventBus.getParallelEventBus().testStarted("app_should_still_work", MyTestCase.class);
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        StepEventBus.getParallelEventBus().testSuiteFinished();
        StepEventBus.getParallelEventBus().testSuiteStarted(MyOtherStory.class);

        StepEventBus.getParallelEventBus().testStarted("app_should_work", MyOtherTestCase.class);
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        TestOutcome outcome = stepListener.getTestOutcomes().get(0);
        assertThat(outcome.getUserStory().getName(), is("MyStory"));
        TestOutcome outcome2 = stepListener.getTestOutcomes().get(1);
        assertThat(outcome2.getUserStory().getName(), is("MyStory"));
        TestOutcome outcome3 = stepListener.getTestOutcomes().get(2);
        assertThat(outcome3.getUserStory().getName(), is("MyOtherStory"));
    }

    @Test
    public void the_listener_should_record_the_tested_story_without_a_class() {

        StepEventBus.getParallelEventBus().testSuiteStarted(MyStory.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work", MyStory.class);

        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        TestOutcome outcome = stepListener.getTestOutcomes().get(0);
        assertThat(outcome.getUserStory().getName(), is("MyStory"));
    }

    @Test
    public void the_listener_should_record_the_tested_story_instance_without_a_class() {

        StepEventBus.getParallelEventBus().testSuiteStarted(net.thucydides.model.domain.Story.from(MyStory.class));
        StepEventBus.getParallelEventBus().testStarted("app should work");

        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        TestOutcome outcome = stepListener.getTestOutcomes().get(0);
        assertThat(outcome.getUserStory().getName(), is("MyStory"));
    }

    @Test
    public void the_listener_should_record_the_tested_story_instance_without_a_class_via_the_test() {

        StepEventBus.getParallelEventBus().testSuiteStarted(MyStory.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work", MyStory.class);
        StepEventBus.getParallelEventBus().testStarted("app should work", net.thucydides.model.domain.Story.from(MyStory.class));

        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        TestOutcome outcome = stepListener.getTestOutcomes().get(0);
        assertThat(outcome.getUserStory().getName(), is("MyStory"));
    }

    @Test
    public void the_listener_should_record_mulitple_tested_story_instances_without_a_class_via_the_tests() {

        StepEventBus.getParallelEventBus().testStarted("app should work", net.thucydides.model.domain.Story.from(MyStory.class));
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        StepEventBus.getParallelEventBus().testStarted("app should still work", net.thucydides.model.domain.Story.from(MyStory.class));
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        StepEventBus.getParallelEventBus().testStarted("app should work", net.thucydides.model.domain.Story.from(AnotherStory.class));
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        TestOutcome outcome = stepListener.getTestOutcomes().get(0);
        assertThat(outcome.getUserStory().getName(), is("MyStory"));

        TestOutcome outcome2 = stepListener.getTestOutcomes().get(1);
        assertThat(outcome2.getUserStory().getName(), is("MyStory"));

        TestOutcome outcome3 = stepListener.getTestOutcomes().get(2);
        assertThat(outcome3.getUserStory().getName(), is("AnotherStory"));

    }

    @Test
    public void the_listener_should_record_a_test_with_the_tested_story_instance_without_a_class() {

        StepEventBus.getParallelEventBus().testSuiteStarted(MyStory.class);
        StepEventBus.getParallelEventBus().testStarted("app should work", MyStory.class);

        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        TestOutcome outcome = stepListener.getTestOutcomes().get(0);
        assertThat(outcome.getUserStory().getName(), is("MyStory"));
    }

    @Test
    public void the_listener_should_record_multiple_tests_with_the_tested_story_instance_without_a_class() {

        StepEventBus.getParallelEventBus().testSuiteStarted(MyStory.class);
        StepEventBus.getParallelEventBus().testStarted("app should work", MyStory.class);
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        StepEventBus.getParallelEventBus().testStarted("app should still work", MyStory.class);
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        TestOutcome outcome = stepListener.getTestOutcomes().get(0);
        assertThat(outcome.getUserStory().getName(), is("MyStory"));

        TestOutcome outcome2 = stepListener.getTestOutcomes().get(1);
        assertThat(outcome2.getUserStory().getName(), is("MyStory"));

    }

    @Test
    public void the_listener_should_record_multiple_tests_and_stories_with_the_tested_story_instance_without_a_class() {

        StepEventBus.getParallelEventBus().testSuiteStarted(MyStory.class);
        StepEventBus.getParallelEventBus().testStarted("app should work", MyStory.class);
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        StepEventBus.getParallelEventBus().testStarted("app should still work", MyStory.class);
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        StepEventBus.getParallelEventBus().testSuiteFinished();
        StepEventBus.getParallelEventBus().testSuiteStarted(MyOtherStory.class);
        StepEventBus.getParallelEventBus().testStarted("the app should work", MyOtherStory.class);
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        TestOutcome outcome1 = stepListener.getTestOutcomes().get(0);
        assertThat(outcome1.getUserStory().getName(), is("MyStory"));

        TestOutcome outcome2 = stepListener.getTestOutcomes().get(1);
        assertThat(outcome2.getUserStory().getName(), is("MyStory"));

        TestOutcome outcome3 = stepListener.getTestOutcomes().get(2);
        assertThat(outcome3.getUserStory().getName(), is("MyOtherStory"));


    }

    @Test
    public void if_no_user_story_is_specified_the_test_case_name_should_be_used_instead() {

        StepEventBus.getParallelEventBus().testSuiteStarted(MyTestCaseWithoutAStory.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps = stepFactory.getSharedStepLibraryFor(FlatScenarioSteps.class);

        steps.step_one();
        steps.step_two();

        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        TestOutcome outcome = stepListener.getTestOutcomes().get(0);
        assertThat(outcome.getUserStory().getName(), is("MyTestCaseWithoutAStory"));
    }


    @Test
    public void you_can_also_specify_the_story_class_directly() {
        StepEventBus.getParallelEventBus().testSuiteStarted(MyStory.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps = stepFactory.getSharedStepLibraryFor(FlatScenarioSteps.class);

        steps.step_one();
        steps.step_two();

        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        TestOutcome outcome = stepListener.getTestOutcomes().get(0);
        net.thucydides.model.domain.Story story = outcome.getUserStory();
        assertThat(story.getStoryClassName(), is(MyStory.class.getName()));
    }


    @Feature
    class MyFeature {
        class MyStoryInAFeature {
        }
    }

    @Story(MyFeature.MyStoryInAFeature.class)
    class MyTestCaseForAFeature {
    }

    @Test
    public void the_test_result_should_record_the_tested_feature() {

        StepEventBus.getParallelEventBus().testSuiteStarted(MyTestCaseForAFeature.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps = stepFactory.getSharedStepLibraryFor(FlatScenarioSteps.class);
        steps.step_one();
        steps.step_two();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        TestOutcome outcome = stepListener.getTestOutcomes().get(0);
        ApplicationFeature feature = outcome.getFeature();
        assertThat(feature.getId(), is(MyFeature.class.getCanonicalName()));
    }

    @Test
    public void the_name_of_the_tested_feature_should_match_the_feature_class() {

        StepEventBus.getParallelEventBus().testSuiteStarted(MyTestCaseForAFeature.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps = stepFactory.getSharedStepLibraryFor(FlatScenarioSteps.class);
        steps.step_one();
        steps.step_two();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        TestOutcome outcome = stepListener.getTestOutcomes().get(0);
        ApplicationFeature feature = outcome.getFeature();
        assertThat(feature.getName(), is("My feature"));
    }

    @Test
    public void the_executed_step_description_should_describe_a_named_executed_step_method() {
        ExecutedStepDescription executedStepDescription
                = new ExecutedStepDescription(FlatScenarioSteps.class, "Step one");

        assertThat(executedStepDescription.getTitle(), is("Step one"));
    }

    @Test
    public void the_executed_step_description_should_return_a_human_readable_name() {
        ExecutedStepDescription executedStepDescription
                = new ExecutedStepDescription(FlatScenarioSteps.class, "stepWithLongName");

        assertThat(executedStepDescription.getTitle(), is("Step with long name"));
    }

    @Test
    public void the_executed_step_description_for_underscored_methods_should_return_a_human_readable_name() {
        ExecutedStepDescription executedStepDescription
                = new ExecutedStepDescription(FlatScenarioSteps.class, "step_with_long_name");

        assertThat(executedStepDescription.getTitle(), is("Step with long name"));
    }

    @Test
    public void the_executed_step_description_should_allow_a_step_without_a_test_class() {
        ExecutedStepDescription executedStepDescription
                = new ExecutedStepDescription("An easyb clause");

        assertThat(executedStepDescription.getTitle(), is("An easyb clause"));
    }

    @Test
    public void the_executed_step_description_should_return_the_corresponding_test_method() {
        ExecutedStepDescription executedStepDescription
                = new ExecutedStepDescription(FlatScenarioSteps.class, "stepWithLongName");

        assertThat(executedStepDescription.getStepMethod().getName(), is("stepWithLongName"));
    }


    @Test(expected = IllegalArgumentException.class)
    public void the_executed_step_description_should_fail_if_no_test_method_exists() {
        ExecutedStepDescription executedStepDescription
                = new ExecutedStepDescription(FlatScenarioSteps.class, "stepWithoutMethod");

        executedStepDescription.getStepMethod();
    }

    @Test
    public void the_executed_step_description_including_parameters_should_return_the_corresponding_test_method() {
        ExecutedStepDescription executedStepDescription
                = new ExecutedStepDescription(FlatScenarioSteps.class, "stepWithParameters: tom");

        assertThat(executedStepDescription.getStepMethod().getName(), is("stepWithParameters"));
    }

    @Test
    public void the_test_outcome_title_should_come_from_the_user_story() {

        StepEventBus.getParallelEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps = stepFactory.getSharedStepLibraryFor(FlatScenarioSteps.class);

        steps.step_one();
        steps.step_two();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);
        assertThat(testOutcome.getTitle(), is("App should work"));
    }

    @Test
    public void when_the_user_story_is_undefined_the_test_outcome_title_should_come_from_the_test_case() {

        StepEventBus.getParallelEventBus().testSuiteStarted(MyTestCaseWithoutAStory.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps = stepFactory.getSharedStepLibraryFor(FlatScenarioSteps.class);
        steps.step_one();
        steps.step_two();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);
        assertThat(testOutcome.getTitle(), is("App should work"));
    }

    @Test
    public void the_step_listener_records_the_test_method_name_if_available() {

        StepEventBus.getParallelEventBus().testSuiteStarted(MyTestCaseWithoutAStory.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work");

        List<TestOutcome> results = stepListener.getTestOutcomes();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        TestOutcome testOutcome = results.get(0);
        assertThat(testOutcome.getName(), is("app_should_work"));
    }

    @Test
    public void the_step_listener_should_record_the_overall_test_result() {

        StepEventBus.getParallelEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps = stepFactory.getSharedStepLibraryFor(FlatScenarioSteps.class);
        steps.step_one();
        steps.step_two();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);

        assertThat(testOutcome.getResult(), is(TestResult.SUCCESS));
    }

    @Test
    public void a_failing_step_should_record_the_failure() {

        StepEventBus.getParallelEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps = stepFactory.getSharedStepLibraryFor(FlatScenarioSteps.class);
        steps.step_one();
        steps.failingStep();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);
        assertThat(testOutcome.getResult(), is(TestResult.FAILURE));
    }

    @Test
    public void should_call_object_methods_normally() {

        StepEventBus.getParallelEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps = stepFactory.getSharedStepLibraryFor(FlatScenarioSteps.class);
        steps.step_one();
        String stringValue = steps.toString();
        steps.step_two();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);
        assertThat(testOutcome.getTestSteps().size(), is(2));
        assertThat(stringValue, is(notNullValue()));
    }

    @Test
    public void should_call_methods_from_a_different_base_package_normally() {

        StepEventBus.getParallelEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work");

        StepsDerivedFromADifferentDomain steps = stepFactory.getSharedStepLibraryFor(StepsDerivedFromADifferentDomain.class);
        steps.step_one();
        String stringValue = steps.returnFoo();
        steps.step_two();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);
        assertThat(testOutcome.getTestSteps().size(), is(2));
        assertThat(stringValue, is("proportionOf"));
    }

    @Test
    public void should_execute_methods_from_same_domain_before_failure() {

        StepEventBus.getParallelEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work");

        StepsInSomeOtherPlace steps = stepFactory.getSharedStepLibraryFor(StepsInSomeOtherPlace.class);
        steps.step_one();
        String stringValue = steps.returnFoo();
        steps.step_two();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);
        assertThat(testOutcome.getTestSteps().size(), is(2));
        assertThat(stringValue, is("proportionOf"));
    }

    @Ignore("We should only skip methods if they call webdriver or make rest calls")
    @Test
    public void should_skip_methods_from_same_domain_after_failure() {

        StepEventBus.getParallelEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work");

        StepsInSomeOtherPlace steps = stepFactory.getSharedStepLibraryFor(StepsInSomeOtherPlace.class);
        steps.step_one();
        steps.step_that_fails();
        String stringValue = steps.returnFoo();
        steps.step_two();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);
        assertThat(testOutcome.getTestSteps().size(), is(3));
        assertThat(stringValue, is(""));
    }

    @Test
    public void should_call_object_methods_normally_after_test_failures() {

        StepEventBus.getParallelEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps = stepFactory.getSharedStepLibraryFor(FlatScenarioSteps.class);
        steps.step_one();
        steps.failingStep();
        String stringValue = steps.toString();
        steps.step_two();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);
        assertThat(testOutcome.getTestSteps().size(), is(3));
        assertThat(stringValue, is(notNullValue()));
    }

    @Test
    public void after_a_failing_step_subsequent_errors_should_be_ignored() {

        StepEventBus.getParallelEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps = stepFactory.getSharedStepLibraryFor(FlatScenarioSteps.class);
        steps.step_one();
        steps.failingStep();
        steps.stepCausingANullPointerException();
        steps.step_two();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        List<TestOutcome> results = stepListener.getTestOutcomes();
        assertThat(results.get(0).getTestSteps().size(), is(4));
    }

    @Test
    public void after_a_failing_step_subsequent_unannotated_errors_should_be_ignored() {

        StepEventBus.getParallelEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps = stepFactory.getSharedStepLibraryFor(FlatScenarioSteps.class);
        steps.step_one();
        steps.failingStep();
        steps.unannotatedStepCausingANullPointerException();
        steps.step_two();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        List<TestOutcome> results = stepListener.getTestOutcomes();
        assertThat(results.get(0).getTestSteps().size(), is(3));
    }

    @Test
    public void a_failing_step_should_record_the_failure_cause() {

        StepEventBus.getParallelEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps = stepFactory.getSharedStepLibraryFor(FlatScenarioSteps.class);
        steps.step_one();
        steps.failingStep();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);
        assertThat(testOutcome.getTestFailureMessage(), is("Step failed"));
    }

    @Test
    public void a_failing_step_should_record_the_failure_even_outside_of_a_step() {

        StepEventBus.getParallelEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work");

        StepEventBus.getParallelEventBus().testFailed(new AssertionError("Test failed"));

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);
        assertThat(testOutcome.getResult(), is(TestResult.FAILURE));
    }

    @Test
    public void a_failing_step_should_record_the_cause_of_a_test_failure() {

        StepEventBus.getParallelEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work");

        StepEventBus.getParallelEventBus().testFailed(new AssertionError("Test failed"));

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);

        assertThat(testOutcome.getTestFailureMessage(), is("Test failed"));
    }

    @Test
    public void a_failing_step_should_record_the_failure_details_with_the_step() {

        StepEventBus.getParallelEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps = stepFactory.getSharedStepLibraryFor(FlatScenarioSteps.class);
        steps.step_one();
        steps.failingStep();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);

        assertThat(testOutcome.getTestSteps().get(1).getResult(), is(TestResult.FAILURE));
        assertThat(testOutcome.getTestSteps().get(1).getException().getErrorType(), is("java.lang.AssertionError"));
        assertThat(testOutcome.getTestSteps().get(1).getErrorMessage(), containsString("Step failed"));
    }

    @Test
    public void a_failing_step_should_not_prevent_collecting_next_steps_result_if_soft_asserts_enabled() throws Exception {

        StepEventBus.getParallelEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work");
        StepEventBus.getParallelEventBus().enableSoftAsserts();

        FlatScenarioSteps steps = stepFactory.getSharedStepLibraryFor(FlatScenarioSteps.class);
        steps.step_one();
        steps.failingWithExceptionStep();
        steps.step_two();
        steps.failingWithExceptionStep();
        steps.step_three();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);

        assertThat(testOutcome.getTestSteps().size(), equalTo(5));
        TestResult[] expectedResults = {TestResult.SUCCESS, TestResult.ERROR, TestResult.SUCCESS, TestResult.ERROR, TestResult.SUCCESS};
        for (int i = 0; i < testOutcome.getTestSteps().size(); i++) {
            assertThat(testOutcome.getTestSteps().get(i).getResult(), is(expectedResults[i]));
        }
    }

    @Test
    public void ignored_tests_should_be_reported() {

        StepEventBus.getParallelEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps = stepFactory.getSharedStepLibraryFor(FlatScenarioSteps.class);
        steps.step_one();
        steps.ignoredStep();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);

        assertThat(testOutcome.getTestSteps().get(1).getResult(), is(TestResult.IGNORED));
    }

    @Test
    public void a_step_can_be_marked_as_pending_programmatically() {

        StepEventBus.getParallelEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps = stepFactory.getSharedStepLibraryFor(FlatScenarioSteps.class);
        steps.step_one();
        steps.programmaticallyPendingStep();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);

        assertThat(testOutcome.getTestSteps().get(1).getResult(), is(TestResult.PENDING));
    }

    @Test
    public void steps_following_a_pending_step_should_be_ignored() {

        StepEventBus.getParallelEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps = stepFactory.getSharedStepLibraryFor(FlatScenarioSteps.class);
        steps.step_one();
        steps.programmaticallyPendingStep();
        steps.step_two();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);

        assertThat(testOutcome.getTestSteps().get(1).getResult(), is(TestResult.PENDING));
        assertThat(testOutcome.getTestSteps().get(2).getResult(), is(TestResult.IGNORED));
    }

    @Test
    public void a_step_can_be_marked_as_ignored_programmatically() {

        StepEventBus.getParallelEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps = stepFactory.getSharedStepLibraryFor(FlatScenarioSteps.class);
        steps.step_one();
        steps.programmaticallyIgnoredStep();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);

        assertThat(testOutcome.getTestSteps().get(1).getResult(), is(TestResult.IGNORED));
    }

    @Test
    public void a_step_following_an_ignored_step_will_be_executed_normally() {

        StepEventBus.getParallelEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps = stepFactory.getSharedStepLibraryFor(FlatScenarioSteps.class);
        steps.step_one();
        steps.programmaticallyIgnoredStep();
        steps.step_two();
        steps.step_three();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);

        assertThat(testOutcome.getTestSteps().get(1).getResult(), is(TestResult.IGNORED));
        assertThat(testOutcome.getTestSteps().get(2).getResult(), is(TestResult.SUCCESS));
        assertThat(testOutcome.getTestSteps().get(3).getResult(), is(TestResult.SUCCESS));
    }

    @Test
    public void grouped_test_steps_should_appear_as_nested() {

        StepEventBus.getParallelEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps = stepFactory.getSharedStepLibraryFor(FlatScenarioSteps.class);
        steps.step_one();
        steps.grouped_steps();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);

        assertThat(testOutcome.toString(), is("App should work:Step one, Grouped steps [Nested step one, Nested step two, Nested step one, Nested step two]"));
    }

    @Test
    public void a_single_group_should_appear_with_nested_steps() {

        StepEventBus.getParallelEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps = stepFactory.getSharedStepLibraryFor(FlatScenarioSteps.class);
        steps.grouped_steps();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);

        assertThat(testOutcome.toString(), is("App should work:Grouped steps [Nested step one, Nested step two, Nested step one, Nested step two]"));
    }

    @Test
    public void deeply_grouped_test_steps_should_appear_as_deeply_nested() {

        StepEventBus.getParallelEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps = stepFactory.getSharedStepLibraryFor(FlatScenarioSteps.class);
        steps.step_one();
        steps.deeply_grouped_steps();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);

        assertThat(testOutcome.toString(), is("App should work:Step one, Deeply grouped steps [Step one, Step two, Grouped steps [Nested step one, Nested step two, Nested step one, Nested step two], Step two, Step one]"));
    }

    @Test
    public void pending_tests_should_be_reported() {

        StepEventBus.getParallelEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps = stepFactory.getSharedStepLibraryFor(FlatScenarioSteps.class);
        steps.step_one();
        steps.pendingStep();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);

        assertThat(testOutcome.toString(), is("App should work:Step one, Pending step"));
        assertThat(testOutcome.getTestSteps().get(1).getResult(), is(TestResult.PENDING));
    }

    @Test
    public void pending_test_groups_should_be_reported() {

        StepEventBus.getParallelEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps = stepFactory.getSharedStepLibraryFor(FlatScenarioSteps.class);

        steps.step_one();
        steps.pending_group();

        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);

        assertThat(testOutcome.toString(), is("App should work:Step one, Pending group [Step three, Step two, Step one]"));
        assertThat(testOutcome.getTestSteps().get(1).getResult(), is(TestResult.PENDING));
    }

    @Test
    public void ignored_test_groups_should_be_skipped() {

        StepEventBus.getParallelEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps = stepFactory.getSharedStepLibraryFor(FlatScenarioSteps.class);

        steps.step_one();
        steps.ignored_group();

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);

        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        assertThat(testOutcome.toString(), is("App should work:Step one, Ignored group [Step three, Step two, Step one]"));
        assertThat(testOutcome.getTestSteps().get(1).getResult(), is(TestResult.IGNORED));
    }

    @Test
    public void a_test_group_with_an_annotated_title_should_record_the_title() {

        StepEventBus.getParallelEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps = stepFactory.getSharedStepLibraryFor(FlatScenarioSteps.class);

        steps.step_with_title();

        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);

        assertThat(testOutcome.getTestSteps().get(0).getDescription(), is("A step with a title"));
    }


    @Test
    public void a_test_group_without_an_annotated_title_should_record_the_humanized_group_name() {

        StepEventBus.getParallelEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps = stepFactory.getSharedStepLibraryFor(FlatScenarioSteps.class);

        steps.a_plain_step_group();

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);

        assertThat(testOutcome.getTestSteps().get(0).getDescription(), is("A plain step group"));
    }

    @Test
    public void succeeding_test_groups_should_be_marked_as_successful_by_default() {

        StepEventBus.getParallelEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps = stepFactory.getSharedStepLibraryFor(FlatScenarioSteps.class);

        steps.grouped_steps();

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);

        assertThat(testOutcome.getTestSteps().get(0).getResult(), is(TestResult.SUCCESS));
    }

    @Test
    public void steps_should_be_skipped_after_a_failure() {

        StepEventBus.getParallelEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps = stepFactory.getSharedStepLibraryFor(FlatScenarioSteps.class);
        steps.step_one();
        steps.failingStep();
        steps.step_two();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);

        assertThat(testOutcome.toString(), is("App should work:Step one, Failing step, Step two"));
        assertThat(testOutcome.getTestSteps().get(2).getResult(), is(TestResult.SKIPPED));
    }

    @Test
    public void steps_should_be_skipped_after_a_failure_in_a_nested_step() {

        StepEventBus.getParallelEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work");

        NestedScenarioSteps steps = stepFactory.getSharedStepLibraryFor(NestedScenarioSteps.class);
        steps.step1();
        steps.nestedFailingStep();
        steps.step2();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);
        assertThat(testOutcome.toString(), is("App should work:Step1 [Step one, Step two, Step three], "
                + "Nested failing step [Failing step], Step2"));

        assertThat(testOutcome.getTestSteps().get(2).getResult(), is(TestResult.SKIPPED));
    }

    @Test
    @Ignore
    // TODO: Get this test working later
    public void steps_should_be_skipped_but_reported_after_a_failure_in_a_nested_step_if_specified() {

        configureEventBus("deep.step.execution.after.failures", "true");

        StepEventBus.getParallelEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work");

        NestedScenarioSteps steps = stepFactory.getSharedStepLibraryFor(NestedScenarioSteps.class);
        steps.step1();
        steps.nestedFailingStep();
        steps.step2();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        StepEventBus.getParallelEventBus().getAllListeners();
        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);
        assertThat(testOutcome.toString(), is("App should work:Step1 [Step one, Step two, Step three], "
                + "Nested failing step [Failing step], Step2 [Step one, Step three]"));

        assertThat(testOutcome.getTestSteps().get(2).getResult(), is(TestResult.SKIPPED));
    }

    @Test
    public void steps_should_not_be_skipped_after_an_ignored_test() {

        StepEventBus.getParallelEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps = stepFactory.getSharedStepLibraryFor(FlatScenarioSteps.class);
        steps.step_one();
        steps.ignoredStep();
        steps.step_two();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);

        assertThat(testOutcome.toString(), is("App should work:Step one, Ignored step, Step two"));
        assertThat(testOutcome.getTestSteps().get(2).getResult(), is(TestResult.SUCCESS));
    }


    @Test
    public void steps_with_failing_nested_steps_should_record_the_step_failure() {

        StepEventBus.getParallelEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work");

        NestedScenarioSteps steps = stepFactory.getSharedStepLibraryFor(NestedScenarioSteps.class);
        steps.step1();
        steps.step_with_nested_failure();


        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);

        assertThat(testOutcome.toString(), is("App should work:Step1 [Step one, Step two, Step three], Step with nested failure [Step one, Failing step]"));
        assertThat(testOutcome.getResult(), is(TestResult.FAILURE));
    }


    @Test
    public void should_return_failing_test_exception() {
        StepEventBus.getParallelEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work");

        NestedScenarioSteps steps = stepFactory.getSharedStepLibraryFor(NestedScenarioSteps.class);
        steps.step1();
        steps.step_with_nested_failure();

        assertThat(stepListener.getTestFailureCause().getMessage(), is("Step failed"));

    }

    @Test
    public void the_legacy_step_group_annotation_can_also_be_used() {

        StepEventBus.getParallelEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps = stepFactory.getSharedStepLibraryFor(FlatScenarioSteps.class);
        steps.a_step_group();


        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);

        assertThat(testOutcome.toString(), is("App should work:Annotated step group title [Step with long name, Step with long name and underscores]"));
    }

    @Test
    public void if_configured_should_pause_after_step() {

        StepEventBus.getParallelEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps = stepFactory.getSharedStepLibraryFor(FlatScenarioSteps.class);

        environmentVariables.setProperty(ThucydidesSystemProperty.SERENITY_STEP_DELAY.getPropertyName(), "100");

        long startTime = System.currentTimeMillis();
        steps.step_one();
        steps.step_two();
        long stepDuration = System.currentTimeMillis() - startTime;

        assertThat((int) stepDuration, greaterThanOrEqualTo(100));
    }


    @Test
    public void the_result_of_a_step_group_with_no_children_should_be_the_group_default_result() {
        TestStep group = new TestStep("Test Group");
        group.setResult(TestResult.SUCCESS);

        assertThat(group.getResult(), is(TestResult.SUCCESS));

    }

    @Test
    public void the_result_of_a_step_group_with_an_undefined_result_is_successful_unless_annotated_otherwise() {
        TestStep group = new TestStep("Test Group");
        group.addChildStep(new TestStep("Child step"));
        assertThat(group.getResult(), is(TestResult.SUCCESS));

    }

    @Test
    public void the_result_of_a_step_group_with_children_should_be_the_result_of_the_children() {
        TestStep group = new TestStep("Test Group");

        group.setResult(TestResult.SUCCESS);


        TestStep testStep = new TestStep();
        testStep.setResult(TestResult.FAILURE);
        group.addChildStep(testStep);

        assertThat(group.getResult(), is(TestResult.FAILURE));

    }


    private void configureEventBus(String property, String value) {
        environmentVariables.setProperty(property, value);
        SystemPropertiesConfiguration configuration = new SystemPropertiesConfiguration(environmentVariables);

        BaseStepListener stepListener = new BaseStepListener(FirefoxDriver.class, outputDirectory, configuration);
        stepListener.setDriver(driver);
        StepEventBus.getParallelEventBus().reset();
        StepEventBus.getParallelEventBus().registerListener(stepListener);
    }

    @Test
    public void screenshots_should_not_be_taken_for_pending_steps() {

        StepEventBus.getParallelEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps = stepFactory.getSharedStepLibraryFor(FlatScenarioSteps.class);
        steps.pendingStep();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        verify(driver, never()).getScreenshotAs((OutputType<?>) ArgumentMatchers.any());
    }


    @Test
    public void screenshots_should_not_be_taken_for_pending_steps_among_implemented_steps() {

        StepEventBus.getParallelEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work");

        FlatScenarioSteps steps = stepFactory.getSharedStepLibraryFor(FlatScenarioSteps.class);
        steps.step_one();
        steps.pendingStep();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        TestOutcome outcome = StepEventBus.getParallelEventBus().getBaseStepListener().getCurrentTestOutcome();
        assertThat(outcome.getTestSteps().get(1).hasScreenshots(), is(false));
    }

    @Test
    public void screenshots_will_be_ignored_if_they_cannot_be_taken() {

        when(driver.getScreenshotAs(any(OutputType.class))).thenThrow(new ScreenshotException("Screenshot failed", null));

        StepEventBus.getParallelEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work");

        NestedScenarioSteps steps = stepFactory.getSharedStepLibraryFor(NestedScenarioSteps.class);
        steps.step1();
        steps.step2();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);
        assertThat(testOutcome.getTestSteps().get(0).getChildren().get(0).getScreenshots().size(), is(0));
        assertThat(testOutcome.getTestSteps().get(0).getChildren().get(1).getScreenshots().size(), is(0));

    }

    @Test
    public void custom_listeners_on_the_classpath_are_registered_automatically() {
        List listeners = StepEventBus.getParallelEventBus().getAllListeners();
        assertThat(containsAnInstanceOf(listeners, SampleStepListener.class), is(true));
    }

    @Test
    public void custom_listeners_in_the_core_thucydides_packages_should_not_be_included() {
        List listeners = StepEventBus.getParallelEventBus().getAllListeners();
        assertThat(containsAnInstanceOf(listeners, ListenerInWrongPackage.class), is(false));
    }

    public boolean containsAnInstanceOf(List<StepListener> listeners, Class listenerClass) {
        for (StepListener listener : listeners) {
            if (listener.getClass() == listenerClass) {
                return true;
            }
        }
        return false;

    }

}
