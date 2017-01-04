package net.thucydides.core.model;

import com.google.common.collect.ImmutableList;
import net.serenitybdd.core.exceptions.TestCompromisedException;
import net.thucydides.core.annotations.Issue;
import net.thucydides.core.annotations.Issues;
import net.thucydides.core.annotations.Story;
import net.thucydides.core.annotations.Title;
import net.thucydides.core.issues.IssueTracking;
import net.thucydides.core.issues.SystemPropertiesIssueTracking;
import net.thucydides.core.model.screenshots.Screenshot;
import net.thucydides.core.model.stacktrace.FailureCause;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;
import net.thucydides.core.steps.StepFailureException;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.MockEnvironmentVariables;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ComparisonFailure;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriverException;

import java.util.Arrays;
import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static net.thucydides.core.matchers.ThucydidesMatchers.hasFilenames;
import static net.thucydides.core.matchers.dates.DateMatchers.isBetween;
import static net.thucydides.core.model.TestResult.*;
import static net.thucydides.core.model.TestStepFactory.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WhenRecordingNewTestOutcomes {


    TestOutcome testOutcome;

    class AUserStory {
    }

    class SimpleTestScenario {
        @Issue("#ISSUE-123")
        public void should_do_this() {
        }

        @Issue("#ISSUE-456")
        public void should_do_that() {
        }

        public void should_do_something_else() {
        }

        @Issue("#789")
        public void should_do_some_other_thing() {
        }
    }

    @Story(AUserStory.class)
    @Issue("#ISSUE-123")
    class SomeTestScenario {
        @Issue("#ISSUE-123")
        public void should_do_this() {
        }

        @Issue("#ISSUE-456")
        public void should_do_that() {
        }

        public void should_do_something_else() {
        }

        @Issue("#789")
        public void should_do_some_other_thing() {
        }
    }

    @Story(AUserStory.class)
    @Issues({"#ISSUE-123", "#ISSUE-456"})
    class SomeOtherTestScenario {
        @Issues({"#ISSUE-123", "#ISSUE-789"})
        public void should_do_this() {
        }

        @Issue("#ISSUE-123")
        public void should_do_that() {
        }

        public void should_do_something_else() {
        }
    }

    @Story(AUserStory.class)
    class SomeAnnotatedTestScenario {
        @Title("Really should do this!")
        public void should_do_this() {
        }

        public void should_do_that() {
        }
    }

    @Story(AUserStory.class)
    class SomeAnnotatedTestScenarioWithAnIssue {
        @Title("Really should do this! (#ISSUE-123)")
        public void should_do_this() {
        }

        public void should_do_that() {
        }

        public void should_do_THIS_as_well() {
        }
    }

    @Story(AUserStory.class)
    class SomeAnnotatedTestScenarioWithManyIssues {
        @Issue("#ISSUE-456")
        @Issues({"#ISSUE-100", "#ISSUE-200"})
        @Title("Really should do this! (#ISSUE-123)")
        public void should_do_this() {
        }

        public void should_do_that() {
        }
    }

    @Story(AUserStory.class)
    @Issue("#ISSUE-100")
    class SomeAnnotatedTestScenarioWithDuplicatedIssues {
        @Issues({"#ISSUE-100", "#ISSUE-200"})
        public void should_do_this() {
        }

        public void should_do_that() {
        }
    }

    @Story(AUserStory.class)
    @Issue("#123")
    class ATestScenarioWithIssuesWithNoPrefix {
        public void should_do_this() {
        }

        @Issue("#456")
        public void should_do_that() {
        }

        public void should_do_something_else() {
        }
    }

    @Before
    public void prepareAcceptanceTestRun() {
        MockitoAnnotations.initMocks(this);
        testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class);
    }

    @Test
    public void a_test_outcome_can_be_initialized_directly_from_a_story() {
        testOutcome = TestOutcome.forTest("should_do_this", AUserStory.class);

        Assert.assertThat(testOutcome.getUserStory().getName(), is("A user story"));
    }

    @Test
    public void a_test_outcome_should_record_the_tested_method_name() {
        TestOutcome outcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class);

        assertThat(outcome.getName(), is("should_do_this"));
    }

    @Test
    public void should_report_failures_or_errors_outside_of_steps() {
        TestOutcome outcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class);
        outcome.recordStep(forASuccessfulTestStepCalled("The user opens the Google search page"));

        assertThat(outcome.hasNonStepFailure(), is(false));
        outcome.determineTestFailureCause(new AssertionError("test failed"));
        assertThat(outcome.hasNonStepFailure(), is(true));
    }

    @Test
    public void a_test_outcome_should_record_the_start_time() {
        DateTime beforeDate = DateTime.now();
        TestOutcome outcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class);
        DateTime afterDate = DateTime.now();
        assertThat(outcome.getStartTime(), isBetween(beforeDate, afterDate));
    }

    /**
     * Case for JUnit integration, where a test case is present.
     */
    @Test
    public void a_test_outcome_title_should_be_based_on_the_tested_method_name_if_defined() {
        TestOutcome outcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class);

        assertThat(outcome.getTitle(), is("Should do this"));
    }

    @Test
    public void a_qualified_test_outcome_title_should_be_based_on_the_tested_method_name_with_a_qualifier() {
        TestOutcome outcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class);

        TestOutcome qualifiedQutcome = outcome.withQualifier("with-this-value");
        assertThat(qualifiedQutcome.getTitle(), is("Should do this [with-this-value]"));
    }

    @Test
    public void a_test_outcome_title_can_be_overriden_using_the_Title_annotation() {
        TestOutcome outcome = TestOutcome.forTest("should_do_this", SomeAnnotatedTestScenario.class);

        assertThat(outcome.getTitle(), is("Really should do this!"));
    }


    @Test
    public void a_test_outcome_result_can_be_overridden() {
        TestOutcome outcome = TestOutcome.forTest("should_do_this", SomeAnnotatedTestScenario.class);
        outcome.setAnnotatedResult(TestResult.IGNORED);

        assertThat(outcome.getResult(), is(TestResult.IGNORED));
    }

    @Test
    public void once_a_test_outcome_result_is_marked_pending_it_cannot_be_redefinedn() {
        TestOutcome outcome = TestOutcome.forTest("should_do_this", SomeAnnotatedTestScenario.class);
        outcome.setAnnotatedResult(TestResult.PENDING);
        outcome.setAnnotatedResult(TestResult.IGNORED);

        assertThat(outcome.getResult(), is(TestResult.PENDING));
    }

    @Test
    public void a_qualified_test_outcome_title_should_contain_the_qualifier() {
        TestOutcome outcome = TestOutcome.forTest("should_do_this", SomeAnnotatedTestScenario.class);
        TestOutcome qualifiedQutcome = outcome.withQualifier("with-this-value");
        assertThat(qualifiedQutcome.getTitle(), is("Really should do this! [with-this-value]"));
    }

    @Test
    public void a_test_outcome_should_record_issue_numbers_from_the_Issue_annotation() {
        TestOutcome outcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class);

        assertThat(outcome.getIssues(), hasItem("#ISSUE-123"));
    }

    @Test
    public void a_test_outcome_should_record_issue_keys_using_the_project_key_if_not_provided() {
        TestOutcome outcome = TestOutcome.forTest("should_do_some_other_thing", SomeTestScenario.class);
        EnvironmentVariables environmentVariables = new MockEnvironmentVariables();

        environmentVariables.setProperty("thucydides.project.key", "ISSUE");
        outcome.setEnvironmentVariables(environmentVariables);


        assertThat(outcome.getIssueKeys(), hasItem("ISSUE-789"));
    }

    @Test
    public void a_test_outcome_should_record_issue_keys_without_the_hash_prefix() {
        TestOutcome outcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class);

        assertThat(outcome.getIssueKeys(), hasItem("ISSUE-123"));
    }

    @Test
    public void a_test_outcome_can_have_no_issues() {
        TestOutcome outcome = TestOutcome.forTest("should_do_this", SomeAnnotatedTestScenario.class);

        assertThat(outcome.getIssues().size(), is(0));
    }

    @Test
    public void a_test_outcome_can_be_associated_with_an_issue() {
        TestOutcome outcome = TestOutcome.forTest("should_do_this", SomeAnnotatedTestScenario.class);

        outcome.isRelatedToIssue("#ISSUE-999");
        outcome.isRelatedToIssue("#ISSUE-000");
        assertThat(outcome.getIssues(), hasItems("#ISSUE-000", "#ISSUE-999"));
    }

    @Mock
    IssueTracking issueTracking;

    @Test
    public void the_test_outcome_title_should_contain_links_to_the_issues() {
        when(issueTracking.getIssueTrackerUrl()).thenReturn("http://my.issue.tracker/MY-PROJECT/browse/ISSUE-{0}");
        TestOutcome outcome = TestOutcome.forTest("should_do_this", SomeAnnotatedTestScenarioWithAnIssue.class).usingIssueTracking(issueTracking);

        assertThat(outcome.getTitleWithLinks() , is("Really should do this! (#<a target=\"_blank\" href=\"http://my.issue.tracker/MY-PROJECT/browse/ISSUE-ISSUE-123\">ISSUE-123</a>)"));
    }

    @Test
    public void the_test_outcome_title_should_respect_acronyms() {
        when(issueTracking.getIssueTrackerUrl()).thenReturn("http://my.issue.tracker/MY-PROJECT/browse/ISSUE-{0}");
        TestOutcome outcome = TestOutcome.forTest("should_do_THIS_as_well", SomeAnnotatedTestScenarioWithAnIssue.class).usingIssueTracking(issueTracking);

        assertThat(outcome.getTitleWithLinks() , is("Should do THIS as well"));
    }


    @Test
    public void should_be_able_to_add_extra_issues() {
        TestOutcome outcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class);
        List<String> extraIssues = Arrays.asList("#ISSUE-456", "#ISSUE-789");
        outcome.addIssues(extraIssues);
        assertThat(outcome.getIssues(), hasItems("#ISSUE-123", "#ISSUE-456", "#ISSUE-789"));
    }

    @Test
    public void a_test_outcome_should_not_inject_issue__links_from_the_Issue_annotation_if_not_configured() {
        TestOutcome outcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class);

        assertThat(outcome.getFormattedIssues(), is("(#ISSUE-123)"));
    }

    @Test
    public void a_test_outcome_should_know_what_issues_there_are() {
        TestOutcome outcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class);

        assertThat(outcome.getIssues(), hasItem("#ISSUE-123"));
    }

    @Test
    public void a_test_outcome_should_know_what_issues_there_are_in_the_title() {
        TestOutcome outcome = TestOutcome.forTest("should_do_this", SomeAnnotatedTestScenarioWithAnIssue.class);

        assertThat(outcome.getIssues(), hasItem("#ISSUE-123"));
    }


    @Test
    public void should_find_all_the_issues_in_a_test() {
        TestOutcome outcome = TestOutcome.forTest("should_do_this", SomeAnnotatedTestScenarioWithManyIssues.class);

        assertThat(outcome.getIssues(), hasItems("#ISSUE-123", "#ISSUE-456", "#ISSUE-100", "#ISSUE-200"));
    }


    @Test
    public void a_test_outcome_should_inject_issue_links_from_the_Issue_annotation_if_requested() {
        MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setProperty("jira.url", "http://my.jira");
        IssueTracking issueTracking = new SystemPropertiesIssueTracking(environmentVariables);

        TestOutcome outcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class)
                .usingIssueTracking(issueTracking);

        assertThat(outcome.getFormattedIssues(), is("(#<a target=\"_blank\" href=\"http://my.jira/browse/ISSUE-123\">ISSUE-123</a>)"));
    }

    @Test
    public void a_test_outcome_should_inject_multiple__issue_links_from_the_Issue_annotation_if_requested() {
        MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setProperty("jira.url", "http://my.jira");
        IssueTracking issueTracking = new SystemPropertiesIssueTracking(environmentVariables);

        TestOutcome outcome = TestOutcome.forTest("should_do_this", SomeOtherTestScenario.class)
                .usingIssueTracking(issueTracking);

        assertThat(outcome.getFormattedIssues(), is("(#<a target=\"_blank\" href=\"http://my.jira/browse/ISSUE-123\">ISSUE-123</a>, #<a target=\"_blank\" href=\"http://my.jira/browse/ISSUE-456\">ISSUE-456</a>, #<a target=\"_blank\" href=\"http://my.jira/browse/ISSUE-789\">ISSUE-789</a>)"));
    }


    @Test
    public void a_test_outcome_should_also_inject_issue_links_from_the_Issue_annotation_at_the_class_level() {
        TestOutcome outcome = TestOutcome.forTest("should_do_that", SomeTestScenario.class);

        assertThat(outcome.getFormattedIssues(), is("(#ISSUE-123, #ISSUE-456)"));
    }

    @Test
    public void a_test_outcome_should_record_multiple_issues() {
        TestOutcome outcome = TestOutcome.forTest("should_do_this", SomeOtherTestScenario.class);

        assertThat(outcome.getFormattedIssues(), is("(#ISSUE-123, #ISSUE-456, #ISSUE-789)"));
    }

    @Test
    public void a_test_outcome_should_record_multiple_issues_and_single_issues() {
        TestOutcome outcome = TestOutcome.forTest("should_do_that", SomeOtherTestScenario.class);

        assertThat(outcome.getFormattedIssues(), is("(#ISSUE-123, #ISSUE-456)"));
    }

    @Test
    public void a_test_outcome_should_record_multiple_issues_at_class_level() {
        TestOutcome outcome = TestOutcome.forTest("should_do_something_else", SomeOtherTestScenario.class);

        assertThat(outcome.getFormattedIssues(), is("(#ISSUE-123, #ISSUE-456)"));
    }

    @Test
    public void a_test_outcome_should_also_inject_issue_links_from_the_Issue_annotation_when_only_at_the_class_level() {
        TestOutcome outcome = TestOutcome.forTest("should_do_something_else", SomeTestScenario.class);

        assertThat(outcome.getFormattedIssues(), is("(#ISSUE-123)"));
    }


    @Test
    public void the_test_outcome_formatted_issues_should_contain_unique_links_to_duplicated_issues() {
        when(issueTracking.getIssueTrackerUrl()).thenReturn("http://my.issue.tracker/MY-PROJECT/browse/{0}");
        TestOutcome outcome = TestOutcome.forTest("should_do_this", SomeAnnotatedTestScenarioWithDuplicatedIssues.class).usingIssueTracking(issueTracking);

        assertThat(outcome.getFormattedIssues() , is("(#<a target=\"_blank\" href=\"http://my.issue.tracker/MY-PROJECT/browse/ISSUE-100\">ISSUE-100</a>, #<a target=\"_blank\" href=\"http://my.issue.tracker/MY-PROJECT/browse/ISSUE-200\">ISSUE-200</a>)"));
    }

    @Test
    public void a_test_outcome_title_can_be_overriden_manually() {
        TestOutcome outcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class);
        outcome.setTitle("My custom title");
        assertThat(outcome.getTitle(), is("My custom title"));
    }

    @Test
    public void a_test_outcome_title_can_be_overriden_manually_even_with_an_annotation() {
        TestOutcome outcome = TestOutcome.forTest("should_do_this", SomeAnnotatedTestScenario.class);
        outcome.setTitle("My custom title");
        assertThat(outcome.getTitle(), is("My custom title"));
    }

    /**
     * Case for easyb integration, where we use a Story class directly.
     */
    @Test
    public void a_test_outcome_title_should_be_the_method_name_if_no_test_class_is_defined() {
        net.thucydides.core.model.Story story = net.thucydides.core.model.Story.from(AUserStory.class);

        TestOutcome outcome = TestOutcome.forTestInStory("Some scenario", story);

        assertThat(outcome.getTitle(), is("Some scenario"));
    }


    @Test
    public void should_record_simple_test_steps() {

        assertThat(testOutcome.getTestSteps().size(), is(0));

        testOutcome.recordStep(forASuccessfulTestStepCalled("The user opens the Google search page"));
        testOutcome.recordStep(forASuccessfulTestStepCalled("The user searches for Cats"));

        assertThat(testOutcome.getTestSteps().toString(),
                is("[The user opens the Google search page, The user searches for Cats]"));
    }

    @Test
    public void should_record_nested_test_steps() {

        testOutcome.recordSteps(ImmutableList.of(forASuccessfulTestStepCalled("Step 1"),forASuccessfulTestStepCalled("Step 2")));
        testOutcome.startGroup();
        testOutcome.recordSteps(ImmutableList.of(forASuccessfulTestStepCalled("Step 2.1"), forASuccessfulTestStepCalled("Step 2.2")));
        testOutcome.endGroup();
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 3"));

        assertThat(testOutcome.getTestSteps().toString(),
                is("[Step 1, Step 2 [Step 2.1, Step 2.2], Step 3]"));
    }

    @Test
    public void should_record_deeply_nested_test_steps() {

        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 1"));
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 2"));
        testOutcome.startGroup();
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 2.1"));
        testOutcome.startGroup();
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 2.1.1"));
        testOutcome.startGroup();
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 2.1.1.1"));
        testOutcome.endGroup();
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 2.1.2"));
        testOutcome.endGroup();
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 2.2"));
        testOutcome.endGroup();
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 3"));

        assertThat(testOutcome.getTestSteps().toString(),
                is("[Step 1, Step 2 [Step 2.1 [Step 2.1.1 [Step 2.1.1.1], Step 2.1.2], Step 2.2], Step 3]"));
    }

    @Test
    public void should_count_test_steps() {

        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 1"));
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 2"));
        testOutcome.startGroup();
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 2.1"));
        testOutcome.startGroup();
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 2.1.1"));
        testOutcome.startGroup();
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 2.1.1.1"));
        testOutcome.endGroup();
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 2.1.2"));
        testOutcome.endGroup();
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 2.2"));
        testOutcome.endGroup();
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 3"));

        assertThat(testOutcome.getStepCount(), is(3));
    }

    @Test
    public void should_count_deeply_nested_test_steps() {
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 1"));
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 2"));
        testOutcome.startGroup();
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 2.1"));
        testOutcome.startGroup();
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 2.1.1"));
        testOutcome.startGroup();
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 2.1.1.1"));
        testOutcome.endGroup();
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 2.1.2"));
        testOutcome.endGroup();
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 2.2"));
        testOutcome.endGroup();
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 3"));

        assertThat(testOutcome.getNestedStepCount(), is(8));
    }

    @Test
    public void the_returned_test_steps_list_should_be_read_only() {
        testOutcome.recordStep(forASuccessfulTestStepCalled("The user opens the Google search page"));

        List<TestStep> testSteps = testOutcome.getTestSteps();
        assertThat(testOutcome.toString(), is("Should do this:The user opens the Google search page"));

        try {
            testSteps.add(new TestStep("Some other step"));
            fail("An UnsupportedOperationException exception should have been thrown");
        } catch (UnsupportedOperationException e) {
            assertThat(testOutcome.toString(), is("Should do this:The user opens the Google search page"));
        }
    }

    @Test
    public void the_acceptance_test_case_is_successful_if_all_the_tests_are_successful() {
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 1"));
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 2"));
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 3"));

        assertThat(testOutcome.getResult(), is(SUCCESS));
        assertThat(testOutcome.isSuccess(), is(true));
    }

    @Test
    public void a_test_case_with_no_steps_should_be_considered_successful_if_no_exceptions_occur() {
        assertThat(testOutcome.getResult(), is(SUCCESS));
        assertThat(testOutcome.isSuccess(), is(true));
        assertThat(testOutcome.isPending(), is(false));

    }

    @Test
    public void should_list_screenshots_in_steps() {
        testOutcome.recordStep(forASuccessfulTestStepCalled("step_1"));
        testOutcome.recordStep(forASuccessfulTestStepCalled("step_2"));
        testOutcome.recordStep(forASuccessfulTestStepCalled("step_3"));

        assertThat(testOutcome.getScreenshots(), hasFilenames("step_1.png", "step_2.png", "step_3.png"));
    }

    @Test
    public void should_know_if_an_outcome_has_screenshots() {
        testOutcome.recordStep(forASuccessfulTestStepCalled("step_1"));
        testOutcome.recordStep(forASuccessfulTestStepCalled("step_2"));
        testOutcome.recordStep(forASuccessfulTestStepCalled("step_3"));

        assertThat(testOutcome.hasScreenshots(), is(true));
    }

    @Test
    public void should_list_screenshots_for_leaf_steps_in_nested_steps() {
        testOutcome.recordStep(forASuccessfulTestStepCalled("step_1"));
        testOutcome.recordStep(forASuccessfulTestStepCalled("step_2"));
        testOutcome.startGroup();
        testOutcome.recordStep(forASuccessfulTestStepCalled("step_2.1"));
        testOutcome.startGroup();
        testOutcome.recordStep(forASuccessfulTestStepCalled("step_2.1.1"));
        testOutcome.startGroup();
        testOutcome.recordStep(forASuccessfulTestStepCalled("step_2.1.1.1"));
        testOutcome.endGroup();
        testOutcome.recordStep(forASuccessfulTestStepCalled("step_2.1.2"));
        testOutcome.endGroup();
        testOutcome.recordStep(forASuccessfulTestStepCalled("step_2.2"));
        testOutcome.endGroup();
        testOutcome.recordStep(forASuccessfulTestStepCalled("step_3"));

        List<String> screenshots = extract(testOutcome.getScreenshots(), on(Screenshot.class).getFilename());
        assertThat(screenshots, hasItems("step_1.png", "step_2.1.1.1.png","step_2.1.2.png", "step_2.2.png", "step_3.png"));
    }

    @Test
    public void a_screenshot_without_an_error_message__returns_an_empty_string() {
        Screenshot screenshot = new Screenshot("step_1.png", "Step 1", 800);
        assertThat(screenshot.getErrorMessage(), is(""));
    }

    @Test
    public void a_failing_screenshot_records_the_error_message() {
        Screenshot screenshot = new Screenshot("step_1.png", "Step 1", 800, new FailureCause(new AssertionError("Element not found")));
        assertThat(screenshot.getErrorMessage(), is("Element not found"));
    }

    @Test
    public void a_failing_step_should_the_error_message_for_errors_with_complex_constructors() {
        Screenshot screenshot = new Screenshot("step_1.png", "Step 1", 800, new FailureCause(new ComparisonFailure("oh crap", "a","b")));
        assertThat(screenshot.getError().getMessage(), is("oh crap expected:<[a]> but was:<[b]>"));
        assertThat(screenshot.getError().getStackTrace().length, is(greaterThan(0)));
    }

    @Test
    public void the_acceptance_test_case_is_a_failure_if_one_test_has_failed() {

        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 1"));
        testOutcome.recordStep(forABrokenTestStepCalled("Step 2", new AssertionError("Oh bother!")));
        testOutcome.recordStep(forASkippedTestStepCalled("Step 3"));

        assertThat(testOutcome.getResult(), is(FAILURE));
        assertThat(testOutcome.isFailure(), is(true));
    }

    @Test
    public void the_acceptance_test_case_is_an_error_if_one_test_has_thrown_a_non_assertion_exception() {

        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 1"));
        testOutcome.recordStep(forABrokenTestStepCalled("Step 2", new WebDriverException("Oh bother!")));
        testOutcome.recordStep(forASkippedTestStepCalled("Step 3"));

        assertThat(testOutcome.getResult(), is(ERROR));
        assertThat(testOutcome.isError(), is(true));
    }

    @Test
    public void the_acceptance_test_case_is_compromised_if_a_compromising_exception_is_raised() {

        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 1"));
        testOutcome.recordStep(forABrokenTestStepCalled("Step 2", new TestCompromisedException("Oh bother!")));
        testOutcome.recordStep(forASkippedTestStepCalled("Step 3"));

        assertThat(testOutcome.getResult(), is(COMPROMISED));
        assertThat(testOutcome.isCompromised(), is(true));

        TestStep compromisedStep = testOutcome.getTestSteps().get(1);
        assertThat(compromisedStep.getErrorMessage(), is("net.serenitybdd.core.exceptions.TestCompromisedException: Oh bother!"));
    }

    @Test
    public void if_a_step_fails_the_error_message_should_be_returned_with_the_result() {

        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 1"));
        testOutcome.recordStep(forABrokenTestStepCalled("Step 2", new AssertionError("Oh bother!")));
        testOutcome.recordStep(forASkippedTestStepCalled("Step 3"));


        TestStep failingStep = testOutcome.getTestSteps().get(1);
        assertThat(failingStep.getErrorMessage(), is("java.lang.AssertionError: Oh bother!"));
    }

    @Test
    public void the_acceptance_test_case_is_failing_if_multiple_tests_have_failed() {

        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 1"));
        testOutcome.recordStep(forABrokenTestStepCalled("Step 2", new AssertionError("Oh bother!")));
        testOutcome.recordStep(forABrokenTestStepCalled("Step 3", new AssertionError("Oh bother!")));
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 4"));

        assertThat(testOutcome.getResult(), is(FAILURE));
    }

    @Test
    public void the_acceptance_test_case_is_failing_if_a_test_step_failed() {

        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 1"));
        testOutcome.recordStep(forABrokenTestStepCalled("Step 2", new StepFailureException("Oh bother",new AssertionError("Oh bother!"))));
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 4"));

        assertThat(testOutcome.getResult(), is(FAILURE));
    }

    @Test
    public void the_acceptance_test_case_is_in_error_if_a_test_step_has_an_error() {

        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 1"));
        testOutcome.recordStep(forABrokenTestStepCalled("Step 2", new StepFailureException("Oh bother",new NoSuchElementException("Can't find element"))));
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 4"));

        assertThat(testOutcome.getResult(), is(ERROR));
    }

    @Test
    public void the_acceptance_test_case_is_in_error_if_multiple_tests_have_failed_or_are_broken() {

        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 1"));
        testOutcome.recordStep(forABrokenTestStepCalled("Step 2", new AssertionError("Oh bother!")));
        testOutcome.recordStep(forABrokenTestStepCalled("Step 3", new WebDriverException("Oh deary me!")));
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 4"));

        assertThat(testOutcome.getResult(), is(ERROR));
    }

    @Test
    public void the_acceptance_test_case_is_pending_if_at_least_one_test_is_pending_and_none_have_failed() {

        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 1"));
        testOutcome.recordStep(forAPendingTestStepCalled("Step 2"));
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 3"));
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 4"));

        assertThat(testOutcome.getResult(), is(PENDING));
    }

    @Test
    public void the_acceptance_test_case_is_failing_if_there_is_a_failure_even_with_pending_test_cases() {

        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 1"));
        testOutcome.recordStep(forAPendingTestStepCalled("Step 2"));
        testOutcome.recordStep(forABrokenTestStepCalled("Step 3", new AssertionError("Oh bother!")));
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 4"));

        assertThat(testOutcome.getResult(), is(FAILURE));
    }

    @Test
    public void the_acceptance_test_case_is_ignored_if_all_test_cases_are_ignored() {

        testOutcome.recordStep(forAnIgnoredTestStepCalled("Step 1"));
        testOutcome.recordStep(forAnIgnoredTestStepCalled("Step 2"));
        testOutcome.recordStep(forAnIgnoredTestStepCalled("Step 3"));
        testOutcome.recordStep(forAnIgnoredTestStepCalled("Step 4"));

        assertThat(testOutcome.getResult(), is(IGNORED));
    }

    @Test
    public void if_one_test_is_ignored_among_others_it_will_not_affect_the_outcome_for_failing_tests() {

        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 1"));
        testOutcome.recordStep(forAPendingTestStepCalled("Step 2"));
        testOutcome.recordStep(forABrokenTestStepCalled("Step 3", new AssertionError("Oh bother!")));
        testOutcome.recordStep(forAnIgnoredTestStepCalled("Step 4"));
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 5"));

        assertThat(testOutcome.getResult(), is(FAILURE));
    }

    @Test
    public void if_one_test_is_ignored_among_others_it_will_not_affect_the_outcome_for_pending_tests() {

        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 1"));
        testOutcome.recordStep(forAPendingTestStepCalled("Step 2"));
        testOutcome.recordStep(forABrokenTestStepCalled("Step 3", new AssertionError("Oh bother!")));
        testOutcome.recordStep(forAnIgnoredTestStepCalled("Step 4"));
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 5"));
        testOutcome.recordStep(forAPendingTestStepCalled("Step 6"));

        assertThat(testOutcome.getResult(), is(FAILURE));
    }

    @Test
    public void if_one_test_is_ignored_among_others_it_will_not_affect_the_outcome_for_successful_tests() {

        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 1"));
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 2"));
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 3"));
        testOutcome.recordStep(forAnIgnoredTestStepCalled("Step 4"));

        assertThat(testOutcome.getResult(), is(SUCCESS));
    }

    @Test
    public void the_model_should_provide_the_number_of_successful_test_steps() {

        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 1"));
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 2"));
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 3"));

        assertThat(testOutcome.getSuccessCount(), is(3));
    }

    @Test
    public void the_model_should_provide_the_number_of_successful_test_steps_in_presence_of_other_outcomes() {

        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 1"));
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 2"));
        testOutcome.recordStep(forAnIgnoredTestStepCalled("Step 3"));
        testOutcome.recordStep(forABrokenTestStepCalled("Step 4", new AssertionError("Oh bother!")));
        testOutcome.recordStep(forASkippedTestStepCalled("Step 5"));

        assertThat(testOutcome.getSuccessCount(), is(2));
    }

    @Test
    public void the_model_should_provide_the_number_of_failed_test_steps() {

        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 1"));
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 2"));
        testOutcome.recordStep(forAnIgnoredTestStepCalled("Step 3"));
        testOutcome.recordStep(forABrokenTestStepCalled("Step 4", new AssertionError("Oh bother!")));
        testOutcome.recordStep(forABrokenTestStepCalled("Step 5", new AssertionError("Oh bother!")));
        testOutcome.recordStep(forASkippedTestStepCalled("Step 6"));

        assertThat(testOutcome.getFailureCount(), is(2));
    }

    @Test
    public void the_model_should_provide_the_number_of_ignored_test_steps() {

        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 1"));
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 2"));
        testOutcome.recordStep(forAnIgnoredTestStepCalled("Step 3"));
        testOutcome.recordStep(forABrokenTestStepCalled("Step 4", new AssertionError("Oh bother!")));
        testOutcome.recordStep(forABrokenTestStepCalled("Step 5", new AssertionError("Oh bother!")));
        testOutcome.recordStep(forASkippedTestStepCalled("Step 6"));
        testOutcome.recordStep(forASkippedTestStepCalled("Step 7"));
        testOutcome.recordStep(forASkippedTestStepCalled("Step 8"));
        testOutcome.recordStep(forASkippedTestStepCalled("Step 9"));

        assertThat(testOutcome.getIgnoredCount(), is(1));
    }

    @Test
    public void the_model_should_provide_the_number_of_skipped_test_steps() {

        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 1"));
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 2"));
        testOutcome.recordStep(forAnIgnoredTestStepCalled("Step 3"));
        testOutcome.recordStep(forABrokenTestStepCalled("Step 4", new AssertionError("Oh bother!")));
        testOutcome.recordStep(forABrokenTestStepCalled("Step 5", new AssertionError("Oh bother!")));
        testOutcome.recordStep(forASkippedTestStepCalled("Step 6"));
        testOutcome.recordStep(forASkippedTestStepCalled("Step 7"));
        testOutcome.recordStep(forASkippedTestStepCalled("Step 8"));
        testOutcome.recordStep(forASkippedTestStepCalled("Step 9"));

        assertThat(testOutcome.getSkippedCount(), is(4));
        assertThat(testOutcome.getSkippedOrIgnoredCount(), is(5));
    }

    @Test
    public void the_model_should_provide_the_number_of_pending_test_steps() {

        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 1"));
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 2"));
        testOutcome.recordStep(forAnIgnoredTestStepCalled("Step 3"));
        testOutcome.recordStep(forABrokenTestStepCalled("Step 4", new AssertionError("Oh bother!")));
        testOutcome.recordStep(forABrokenTestStepCalled("Step 5", new AssertionError("Oh bother!")));
        testOutcome.recordStep(forASkippedTestStepCalled("Step 6"));
        testOutcome.recordStep(forAPendingTestStepCalled("Step 7"));
        testOutcome.recordStep(forAPendingTestStepCalled("Step 8"));
        testOutcome.recordStep(forAPendingTestStepCalled("Step 9"));

        assertThat(testOutcome.getPendingCount(), is(3));
    }


    @Test
    public void a_test_run_with_only_successful_tests_is_successful() {

        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 1"));
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 2"));
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 3"));

        assertThat(testOutcome.getResult(), is(TestResult.SUCCESS));
    }

    @Test
    public void an_acceptance_test_run_can_contain_steps_nested_in_step_groups() {
        testOutcome.recordStep(forASuccessfulTestStepCalled("A Group"));
        testOutcome.startGroup();
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 1"));
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 2"));
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 3"));
        testOutcome.endGroup();

        assertThat(testOutcome.getTestSteps().size(), is(1));
    }


    private void createNestedTestSteps() {
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 0"));
        testOutcome.recordStep(forASuccessfulTestStepCalled("A group"));
        testOutcome.startGroup();
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 1"));
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 2"));
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 3"));
        testOutcome.recordStep(forASuccessfulTestStepCalled("Another group"));
        testOutcome.startGroup();
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 4"));
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 5"));
        testOutcome.endGroup();
        testOutcome.endGroup();
    }

    @Test
    public void an_acceptance_test_run_can_contain_step_groups_nested_in_step_groups() {
        createNestedTestSteps();
        assertThat(testOutcome.getTestSteps().size(), is(2));
    }

    @Test
    public void when_test_steps_are_nested_step_count_should_include_all_steps() {
        createNestedTestSteps();
        assertThat(testOutcome.countTestSteps(), is(6));
    }

    @Test
    public void an_acceptance_test_run_can_count_all_the_successful_nested_test_steps() {
        createNestedTestRun();
        assertThat(testOutcome.getSuccessCount(), is(6));
    }

    @Test
    public void an_acceptance_test_run_can_count_all_the_failing_nested_test_steps() {
        createNestedTestRun();

        assertThat(testOutcome.getFailureCount(), is(3));
    }

    @Test
    public void an_acceptance_test_run_can_count_all_the_pending_nested_test_steps() {
        createNestedTestRun();

        assertThat(testOutcome.getPendingCount(), is(4));
    }

    @Test
    public void an_acceptance_test_run_can_count_all_the_ignored_nested_test_steps() {
        createNestedTestRun();

        assertThat(testOutcome.getIgnoredCount(), is(1));
    }

    @Test
    public void an_acceptance_test_run_can_count_all_the_skipped_test_steps() {
        createNestedTestRun();

        assertThat(testOutcome.getSkippedCount(), is(1));
    }

    private void createNestedTestRun() {
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 0"));
        testOutcome.recordStep(forASuccessfulTestStepCalled("A group"));
        testOutcome.startGroup();
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 1"));
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 2"));
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 3"));
        testOutcome.recordStep(forABrokenTestStepCalled("Step 7", new AssertionError("Oh bother!")));
        testOutcome.recordStep(forAPendingTestStepCalled("Step 10"));
        testOutcome.recordStep(forASuccessfulTestStepCalled("Another group"));
        testOutcome.startGroup();
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 4"));
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 5"));
        testOutcome.recordStep(forAnIgnoredTestStepCalled("Step 6"));
        testOutcome.recordStep(forABrokenTestStepCalled("Step 7", new AssertionError("Oh bother!")));
        testOutcome.recordStep(forABrokenTestStepCalled("Step 8", new AssertionError("Oh bother!")));
        testOutcome.recordStep(forASkippedTestStepCalled("Step 9"));
        testOutcome.recordStep(forAPendingTestStepCalled("Step 10"));
        testOutcome.recordStep(forAPendingTestStepCalled("Step 11"));
        testOutcome.recordStep(forAPendingTestStepCalled("Step 12"));
        testOutcome.endGroup();
        testOutcome.endGroup();
    }

    @Test
    public void a_test_group_with_only_successful_tests_is_successful() {

        testOutcome.recordStep(forASuccessfulTestStepCalled("A group"));
        testOutcome.startGroup();
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 1"));
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 2"));
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 3"));
        testOutcome.endGroup();

        TestStep aGroup = testOutcome.getTestSteps().get(0);
        assertThat(aGroup.getResult(), is(TestResult.SUCCESS));
    }


    @Test
    public void a_test_group_with_a_failing_test_fails() {

        testOutcome.startGroup("A group");
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 1"));
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 2"));
        testOutcome.recordStep(forABrokenTestStepCalled("Step 3", new AssertionError("Oh bother!")));
        testOutcome.recordStep(forASkippedTestStepCalled("Step 4"));
        testOutcome.recordStep(forAnIgnoredTestStepCalled("Step 5"));
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 4"));
        testOutcome.endGroup();

        TestStep aGroup = testOutcome.getTestSteps().get(0);
        assertThat(aGroup.getResult(), is(TestResult.FAILURE));
    }


    @Test
    public void a_test_group_with_a_pending_test_is_pending() {

        testOutcome.startGroup("A group");
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 1"));
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 2"));
        testOutcome.recordStep(forAPendingTestStepCalled("Step 3"));
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 4"));
        testOutcome.endGroup();

        TestStep aGroup = testOutcome.getTestSteps().get(0);
        assertThat(aGroup.getResult(), is(TestResult.PENDING));
    }

    @Test
    public void a_test_group_with_only_ignored_tests_is_ignored() {

        testOutcome.startGroup("A group");
        testOutcome.recordStep(forAnIgnoredTestStepCalled("Step 1"));
        testOutcome.recordStep(forAnIgnoredTestStepCalled("Step 2"));
        testOutcome.recordStep(forAnIgnoredTestStepCalled("Step 3"));
        testOutcome.endGroup();
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 4"));

        TestStep aGroup = testOutcome.getTestSteps().get(0);
        assertThat(aGroup.getResult(), is(TestResult.IGNORED));
    }

    @Test
    public void a_test_run_with_a_nested_group_containing_a_failure_is_a_failure() {
        testOutcome.startGroup("A group");
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 1"));
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 2"));
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 3"));
        testOutcome.startGroup("Another group");
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 4"));
        testOutcome.recordStep(forABrokenTestStepCalled("Step 5", new AssertionError("Oh bother!")));
        testOutcome.endGroup();
        testOutcome.endGroup();

        assertThat(testOutcome.getResult(), is(TestResult.FAILURE));
    }

    @Test
    public void a_test_group_with_a_nested_group_containing_a_failure_is_a_failure() {
        testOutcome.startGroup("A group");
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 1"));
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 2"));
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 3"));
        testOutcome.startGroup("Another group");
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 4"));
        testOutcome.recordStep(forABrokenTestStepCalled("Step 5", new AssertionError("Oh bother!")));
        testOutcome.endGroup();
        testOutcome.endGroup();

        TestStep aGroup = testOutcome.getTestSteps().get(0);
        assertThat(aGroup.getResult(), is(TestResult.FAILURE));
    }

    class MyApp {
        class MyUserStory {
        }
    }

    @Test
    public void an_acceptance_test_relates_to_a_user_story() {
        net.thucydides.core.model.Story story = net.thucydides.core.model.Story.from(MyApp.MyUserStory.class);
        TestOutcome testOutcome = TestOutcome.forTestInStory("some_test", story);

        assertThat(testOutcome.getUserStory().getName(), is("My user story"));
    }

    @Test
    public void an_acceptance_test_title_is_the_title_of_the_user_story() {
        net.thucydides.core.model.Story story = net.thucydides.core.model.Story.from(MyApp.MyUserStory.class);
        TestOutcome testOutcome = TestOutcome.forTestInStory("some_test", story);

        assertThat(testOutcome.getStoryTitle(), is("My user story"));
    }

    @Test
    public void the_complete_test_name_should_include_the_story_and_the_method_name() {
        net.thucydides.core.model.Story story = net.thucydides.core.model.Story.from(MyApp.MyUserStory.class);
        TestOutcome testOutcome = TestOutcome.forTestInStory("some_test", story);
        assertThat(testOutcome.getCompleteName(), is("My user story:some_test"));
    }

    @Test
    public void the_complete_test_name_should_include_the_test_case_if_defined_and_the_method_name() {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SimpleTestScenario.class);
        assertThat(testOutcome.getCompleteName(), is("Simple test scenario:should_do_this"));
    }

    @Test
    public void an_acceptance_test_records_the_original_story_class() {
        net.thucydides.core.model.Story story = net.thucydides.core.model.Story.from(MyApp.MyUserStory.class);
        TestOutcome testOutcome = TestOutcome.forTestInStory("some_test", story);
        assertThat(testOutcome.getUserStory().getStoryClassName(), is(MyApp.MyUserStory.class.getName()));
    }

    @Test
    public void we_can_record_the_lifetime_of_a_test_run() throws InterruptedException {
        Thread.sleep(100);
        testOutcome.recordDuration();
        assertThat(testOutcome.getDuration(), is(greaterThanOrEqualTo(10L)));
        assertThat(testOutcome.getDuration(), is(lessThan(5000L)));
        assertThat(testOutcome.getDurationInSeconds(), is(lessThan(5.0)));
    }

    class SimpleScenarioSteps extends ScenarioSteps {

        public SimpleScenarioSteps(final Pages pages) {
            super(pages);
        }
    }

    @Test
    public void scenario_steps_should_have_a_sensible_toString() {
        Pages pages = mock(Pages.class);
        SimpleScenarioSteps steps = new SimpleScenarioSteps(pages);

        assertThat(steps.toString(), is("SimpleScenarioSteps"));
    }

    @Test
    public void should_be_able_to_find_the_last_step() {
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 1"));
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 2"));
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 3"));

        assertThat(testOutcome.lastStep().getDescription(), is("Step 3"));
    }


    @Test
    public void should_be_able_to_find_the_last_step_in_a_group() {

        testOutcome.recordStep(forASuccessfulTestStepCalled("Group 1"));
        testOutcome.startGroup();
        testOutcome.recordStep(forASuccessfulTestStepCalled("Group 2"));
        testOutcome.startGroup();
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 1"));
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 2"));
        testOutcome.recordStep(forASuccessfulTestStepCalled("Step 3"));

        assertThat(testOutcome.lastStep().getDescription(), is("Step 3"));
    }

//    @Test
//    public void should_calculate_the_overall_success_rate_from_provided_statistics() {
//        TestStatistics statistics = new TestStatistics(10L, 7L, 3L,
//                                                       ImmutableList.of(SUCCESS,SUCCESS,SUCCESS,SUCCESS,SUCCESS,SUCCESS,SUCCESS,FAILURE,FAILURE,FAILURE),
//                                                       ImmutableList.of(new TestRunTag("MYPROJECT","A story","story")));
//
//        testOutcome.setStatistics(statistics);
//        assertThat(testOutcome.getOverallStability(), is(0.7));
//    }
//
//    @Test
//    public void should_calculate_the_recent_success_rate_from_provided_statistics() {
//        TestStatistics statistics = new TestStatistics(10L, 7L, 3L,
//                ImmutableList.of(SUCCESS,SUCCESS,SUCCESS,SUCCESS,SUCCESS,SUCCESS,SUCCESS,FAILURE,FAILURE,FAILURE),
//                ImmutableList.of(new TestRunTag("MYPROJECT","A story","story")));
//
//        testOutcome.setStatistics(statistics);
//        assertThat(testOutcome.getRecentStability(), is(0.7));
//    }
//
//    @Test
//    public void should_count_the_recent_test_runs_from_provided_statistics() {
//        TestStatistics statistics = new TestStatistics(8L, 5L, 3L,
//                ImmutableList.of(FAILURE,FAILURE,PENDING,SUCCESS,SUCCESS,SUCCESS,SUCCESS,SUCCESS),
//                ImmutableList.of(new TestRunTag("MYPROJECT","A story","story")));
//
//        testOutcome.setStatistics(statistics);
//        assertThat(testOutcome.getRecentTestRunCount() , is(8L));
//        assertThat(testOutcome.getRecentFailCount() , is(2));
//        assertThat(testOutcome.getRecentPassCount() , is(5));
//        assertThat(testOutcome.getRecentPendingCount() , is(1));
//    }
}
