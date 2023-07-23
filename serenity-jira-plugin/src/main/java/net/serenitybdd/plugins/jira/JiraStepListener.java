package net.serenitybdd.plugins.jira;

import com.google.inject.Inject;
import net.serenitybdd.plugins.jira.guice.Injectors;
import net.serenitybdd.plugins.jira.model.IssueTracker;
import net.serenitybdd.plugins.jira.workflow.WorkflowLoader;
import net.thucydides.core.model.*;
import net.thucydides.core.screenshots.ScreenshotAndHtmlSource;
import net.thucydides.core.steps.ExecutedStepDescription;
import net.thucydides.core.steps.StepFailure;
import net.thucydides.core.steps.StepListener;
import net.thucydides.core.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Updates JIRA issues referenced in a story with a link to the corresponding story report.
 */
public class JiraStepListener implements StepListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(JiraStepListener.class);

    private static TestResultTally<TestOutcomeSummary> resultTally = new TestResultTally<>();
    private static Set<String> testSuiteIssues = new CopyOnWriteArraySet();
    private JiraUpdater jiraUpdater;

    @Inject
    public JiraStepListener(IssueTracker issueTracker,
                            EnvironmentVariables environmentVariables,
                            WorkflowLoader loader) {
        jiraUpdater = new JiraUpdater(issueTracker,environmentVariables,loader);
    }

    public JiraStepListener() {
        this(Injectors.getInjector().getInstance(IssueTracker.class),
                Injectors.getInjector().getProvider(EnvironmentVariables.class).get(),
                Injectors.getInjector().getInstance(WorkflowLoader.class));
    }

    public void testSuiteStarted(final Class<?> testCase) {
        testSuiteIssues.clear();
    }

    public void testSuiteStarted(final Story story) {
        testSuiteIssues.clear();
    }

    public void testStarted(final String testName) {
    }

    @Override
    public void testStarted(String description, String id) {

    }

    @Override
    public void testStarted(String description, String id, ZonedDateTime startTime) {

    }

    public void testFinished(TestOutcome result) {
        if (jiraUpdater.shouldUpdateIssues()) {
            List<String> issues = jiraUpdater.getPrefixedIssuesWithoutHashes(new TestOutcomeSummary(result));
            tallyResults(new TestOutcomeSummary(result), issues);
            testSuiteIssues.addAll(issues);
        }
    }

    @Override
    public void testFinished(TestOutcome result, boolean isInDataDrivenTest, ZonedDateTime finishTime) {

    }

    private void tallyResults(TestOutcomeSummary result, List<String> issues) {
        for(String issue : issues) {
            resultTally.recordResult(issue, result);
        }
    }

    public void testSuiteFinished() {
        if (jiraUpdater.shouldUpdateIssues()) {
            jiraUpdater.updateIssueStatus(testSuiteIssues, resultTally);
        }
    }

    public void testRetried() {}

    public void stepStarted(ExecutedStepDescription executedStepDescription) {}

    public void skippedStepStarted(ExecutedStepDescription description) {}

    public void stepFailed(StepFailure stepFailure) {}

    @Override
    public void stepFailed(StepFailure failure, List<ScreenshotAndHtmlSource> screenshotList) {

    }

    public void lastStepFailed(StepFailure stepFailure) {}

    public void stepIgnored() {}

    public void stepIgnored(String s) {}

    public void stepPending() {}

    public void stepPending(String s) {}

    public void assumptionViolated(String s) {}

    public void testRunFinished() {
    }

    @Override
    public void takeScreenshots(List<ScreenshotAndHtmlSource> screenshots) {

    }

    @Override
    public void takeScreenshots(TestResult testResult, List<ScreenshotAndHtmlSource> screenshots) {

    }

    public void stepFinished() {}

    @Override
    public void stepFinished(List<ScreenshotAndHtmlSource> screenshotList) {

    }

    public void testFailed(TestOutcome testOutcome, Throwable cause) {}

    public void testIgnored() {}

    @Override
    public void testSkipped() {}

    @Override
    public void testPending() {}

    @Override
    public void testIsManual() {}

    public void notifyScreenChange() {}

    public void useExamplesFrom(DataTable dataTable) {}

    @Override
    public void addNewExamplesFrom(DataTable dataTable) {}

    public void exampleStarted(Map<String, String> stringStringMap) {}

    public void exampleStarted() {}

    public void exampleFinished() {}

    public TestResultTally getTestResultTally(){
        return resultTally;
    }

    public Set<String> getTestSuiteIssues() {
        return testSuiteIssues;
    }

    public JiraUpdater getJiraUpdater()
    {
        return jiraUpdater;
    }
}
