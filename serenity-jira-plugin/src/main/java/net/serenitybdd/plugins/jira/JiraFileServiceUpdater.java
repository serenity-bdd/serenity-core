package net.serenitybdd.plugins.jira;


import com.google.common.collect.Lists;
import com.google.inject.Inject;
import net.serenitybdd.plugins.jira.guice.Injectors;
import net.serenitybdd.plugins.jira.model.IssueTracker;
import net.serenitybdd.plugins.jira.workflow.WorkflowLoader;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestOutcomeSummary;
import net.thucydides.core.reports.JiraUpdaterService;
import net.thucydides.core.reports.TestOutcomeStream;
import net.thucydides.core.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Loads test outcomes from a given directory and updates Jira issues comments and Jira issues workflow status.
 */
public class JiraFileServiceUpdater implements JiraUpdaterService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JiraFileServiceUpdater.class);
    private final TestResultTally<TestOutcomeSummary> resultTally;
    private Set<String> allIssues;
    private JiraUpdater jiraUpdater;

    @Inject
    public JiraFileServiceUpdater(IssueTracker issueTracker,
                                  EnvironmentVariables environmentVariables,
                                  WorkflowLoader loader) {
        this.resultTally = new TestResultTally<TestOutcomeSummary>();
        this.allIssues = new HashSet<>();
        jiraUpdater = new JiraUpdater(issueTracker,environmentVariables,loader);
    }

    public JiraFileServiceUpdater() {
        this(Injectors.getInjector().getInstance(IssueTracker.class),
                Injectors.getInjector().getProvider(EnvironmentVariables.class).get() ,
                Injectors.getInjector().getInstance(WorkflowLoader.class));
    }

    /**
     * Loads test results from outcome directory and updates Jira comments and workflow.
     * @param outcomeDirectory  - test outcome directory.
     * @throws IOException
     */
    public List<TestOutcomeSummary> updateJiraForTestResultsFrom(String outcomeDirectory) throws IOException
    {
        LOGGER.info("Update Jira for test results from " + outcomeDirectory);
        List<TestOutcomeSummary> testOutcomeSummaries = loadTestOutcomesSummariesFromPath(outcomeDirectory);
        for(TestOutcomeSummary currentTestOutcomeSummary : testOutcomeSummaries)
        {
            loadTestOutcomeSummary(currentTestOutcomeSummary);
        }
        updateAllIssuesStatus();
        return testOutcomeSummaries;
    }

    /**
     *  Loads test results from outcome directory (matching the given regular expression filter for TestOutcomes names)
     *  and updates Jira comments and workflow.
     *
     * @param outcomeDirectory test outcome directory.
     * @param outcomesNameFilter - regular expression filter for the outcomes names.
     * @throws IOException
     */
    public List<TestOutcomeSummary> updateJiraForTestResultsFrom(String outcomeDirectory, String outcomesNameFilter) throws IOException
    {
        List<TestOutcomeSummary> testOutcomeSummaries = loadTestOutcomesSummariesFromPath(outcomeDirectory);
        for(TestOutcomeSummary currentTestOutcomeSummary : testOutcomeSummaries)
        {
            if(currentTestOutcomeSummary.getName().matches(outcomesNameFilter)) {
                loadTestOutcomeSummary(currentTestOutcomeSummary);

            }
        }
        updateAllIssuesStatus();
        return testOutcomeSummaries;
    }

    private static List<TestOutcomeSummary> loadTestOutcomesSummariesFromPath(String outcomesPath) throws IOException {
        List<TestOutcomeSummary> testOutcomes = Lists.newArrayList();
        Path directory = Paths.get(outcomesPath);
        try(TestOutcomeStream stream = TestOutcomeStream.testOutcomesInDirectory(directory)) {
            for(TestOutcome outcome : stream) {
                testOutcomes.add(new TestOutcomeSummary(outcome,directory));
            }
        }
        return testOutcomes;
    }

    private void loadTestOutcomeSummary(TestOutcomeSummary outcomeSummary) {
        if (jiraUpdater.shouldUpdateIssues()) {
            List<String> issues = jiraUpdater.getPrefixedIssuesWithoutHashes(outcomeSummary);
            tallyResults(outcomeSummary, issues);
            allIssues.addAll(issues);
        }
    }

    private void updateAllIssuesStatus() {
        if (jiraUpdater.shouldUpdateIssues()) {
            jiraUpdater.updateIssueStatus(allIssues,resultTally);
            //allIssues.clear();
        }
    }
    private void tallyResults(TestOutcomeSummary result, List<String> issues) {
        for(String issue : issues) {
            resultTally.recordResult(issue, result);
        }
    }

    public JiraUpdater getJiraUpdater()
    {
        return jiraUpdater;
    }
}
