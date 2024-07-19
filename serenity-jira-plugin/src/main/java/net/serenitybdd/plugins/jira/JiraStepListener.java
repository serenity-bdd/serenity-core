package net.serenitybdd.plugins.jira;


import net.serenitybdd.model.di.ModelInfrastructure;
import net.serenitybdd.plugins.jira.model.IssueTracker;
import net.serenitybdd.plugins.jira.service.JIRAInfrastructure;
import net.serenitybdd.plugins.jira.workflow.WorkflowLoader;
import net.thucydides.model.domain.*;
import net.thucydides.model.steps.StepListenerAdapter;
import net.thucydides.model.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Updates JIRA issues referenced in a story with a link to the corresponding story report.
 */
public class JiraStepListener extends StepListenerAdapter {

    private final TestResultTally<TestOutcomeSummary> resultTally = new TestResultTally<>();
    private final Set<String> testSuiteIssues = new CopyOnWriteArraySet<>();
    private final JiraUpdater jiraUpdater;

    private static final Logger LOGGER = LoggerFactory.getLogger(JiraStepListener.class);

    
    public JiraStepListener(IssueTracker issueTracker,
                            EnvironmentVariables environmentVariables,
                            WorkflowLoader loader) {
        jiraUpdater = new JiraUpdater(issueTracker,environmentVariables,loader);
    }

    public JiraStepListener() {
        this(JIRAInfrastructure.getIssueTracker(),
                ModelInfrastructure.getEnvironmentVariables(),
                JIRAInfrastructure.getWorkflowLoader());
    }

    public void testSuiteStarted(final Class<?> testCase) {
        testSuiteIssues.clear();
    }

    public void testSuiteStarted(final Story story) {
        testSuiteIssues.clear();
    }


    @Override
    public void testFinished(TestOutcome result) {
        boolean shouldUpdateIssues = jiraUpdater.shouldUpdateIssues();
        LOGGER.info("TestFinished updateIssues=" + shouldUpdateIssues);
        if (shouldUpdateIssues) {
            List<String> issues = jiraUpdater.getPrefixedIssuesWithoutHashes(new TestOutcomeSummary(result));
            tallyResults(new TestOutcomeSummary(result), issues);
            testSuiteIssues.addAll(issues);
        }
    }

    @Override
    public void testFinished(TestOutcome result, boolean isInDataDrivenTest, ZonedDateTime finishTime) {
        testFinished(result);
    }

    @Override
    public void testFinished(TestOutcome result, boolean isInDataDrivenTest) {
        testFinished(result);
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
