package net.serenitybdd.plugins.jira;


import net.serenitybdd.plugins.jira.domain.IssueComment;
import net.serenitybdd.plugins.jira.model.IssueTracker;
import net.serenitybdd.plugins.jira.model.NamedTestResult;
import net.serenitybdd.plugins.jira.model.TestResultComment;
import net.serenitybdd.plugins.jira.service.JIRAConfiguration;
import net.serenitybdd.plugins.jira.service.JIRAInfrastructure;
import net.serenitybdd.plugins.jira.service.NoSuchIssueException;
import net.serenitybdd.plugins.jira.workflow.ClasspathWorkflowLoader;
import net.serenitybdd.plugins.jira.workflow.Workflow;
import net.serenitybdd.plugins.jira.workflow.WorkflowLoader;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.model.TestOutcomeSummary;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static net.serenitybdd.plugins.jira.JiraPluginConfigurationOptions.*;
import static net.serenitybdd.plugins.jira.model.JIRACommentBuilder.SERENITY_COMMENT_HEADING;

/**
 * Class used for JIra interaction, to update comments in Jira issues.
 */
public class JiraUpdater {

    static int DEFAULT_MAX_THREADS = 4;
    private final IssueTracker issueTracker;

    private final EnvironmentVariables environmentVariables;
    private static final Logger LOGGER = LoggerFactory.getLogger(JiraUpdater.class);
    private final String projectPrefix;
    private final Workflow workflow;
    private final JIRAConfiguration configuration;

    public JiraUpdater(IssueTracker issueTracker,
                       EnvironmentVariables environmentVariables,
                       WorkflowLoader loader) {
        this.issueTracker = issueTracker;
        this.environmentVariables = environmentVariables;
        configuration = JIRAInfrastructure.getConfiguration();
        workflow = loader.load();
        this.projectPrefix = environmentVariables.getProperty(ThucydidesSystemProperty.JIRA_PROJECT.getPropertyName());
        logStatus(environmentVariables);
    }

    private void logStatus(EnvironmentVariables environmentVariables) {
        String jiraUrl = environmentVariables.getProperty(ThucydidesSystemProperty.JIRA_URL.getPropertyName());
        String reportUrl = ThucydidesSystemProperty.SERENITY_PUBLIC_URL.from(environmentVariables, "");
        LOGGER.debug("JIRA LISTENER STATUS");
        LOGGER.debug("JIRA URL: {} ", jiraUrl);
        LOGGER.debug("REPORT URL: {} ", reportUrl);
        LOGGER.debug("WORKFLOW ACTIVE: {} ", workflow.isActive());
    }

    public void updateIssueStatus(Set<String> issues, final TestResultTally<TestOutcomeSummary> resultTally) {

        issues.parallelStream().forEach(
                issue -> updateIssue(issue, resultTally.getTestOutcomesForIssue(issue))
        );
    }

    public boolean shouldUpdateIssues() {
        if (dryRun()) {
            return false;
        }
        String jiraUrl = environmentVariables.getProperty(ThucydidesSystemProperty.JIRA_URL.getPropertyName());
        String reportUrl = ThucydidesSystemProperty.SERENITY_PUBLIC_URL.from(environmentVariables, "");
        if (workflow.isActive()) {
            LOGGER.debug("WORKFLOW TRANSITIONS: {}", workflow.getTransitions());
        }
        return !(StringUtils.isEmpty(jiraUrl) || StringUtils.isEmpty(reportUrl));
    }

    private void updateIssue(String issueId, List<TestOutcomeSummary> testOutcomes) {

        try {
            TestResultComment testResultComment = newOrUpdatedCommentFor(issueId, testOutcomes);
            if (getWorkflow().isActive() && shouldUpdateWorkflow()) {
                updateIssueStatusFor(issueId, testResultComment.getOverallResult());
            }
        } catch (NoSuchIssueException e) {
            LOGGER.error("No JIRA issue found with ID {}", issueId);
        }
    }

    private void updateIssueStatusFor(final String issueId, final TestResult testResult) {
        LOGGER.info("Updating status for issue {} with test result {}", issueId, testResult);
        String currentStatus = issueTracker.getStatusFor(issueId);

        LOGGER.info("Issue {} currently has status '{}'", issueId, currentStatus);

        List<String> transitions = getWorkflow().getTransitions().forTestResult(testResult).whenIssueIs(currentStatus);
        LOGGER.info("Found transitions {} for issue {}", transitions, issueId);

        for (String transition : transitions) {
            issueTracker.doTransition(issueId, transition);
        }
    }

    private List<NamedTestResult> namedTestResultsFrom(List<TestOutcomeSummary> testOutcomes) {
        return testOutcomes.stream().map(testOutcome -> new NamedTestResult(testOutcome.getTitle(),testOutcome.getTestResult())).collect(Collectors.toList());
    }

    private TestResultComment newOrUpdatedCommentFor(final String issueId, List<TestOutcomeSummary> testOutcomes) {
        LOGGER.info("Updating comments for issue {}", issueId);
        LOGGER.info("WIKI Rendering activated: {}", isWikiRenderedActive());

        List<IssueComment> comments = issueTracker.getCommentsFor(issueId);
        Optional<IssueComment> existingComment = findExistingSerenityCommentIn(comments);
        String testRunNumber = environmentVariables.getProperty(BUILD_ID_PROPERTY);
        TestResultComment testResultComment;
        List<NamedTestResult> newTestResults = namedTestResultsFrom(testOutcomes);
        if (!existingComment.isPresent() || createNewCommentForEachUpdate()) {
            testResultComment = TestResultComment.comment(isWikiRenderedActive())
                    .withResults(namedTestResultsFrom(testOutcomes))
                    .withReportUrl(linkToReport(testOutcomes))
                    .forTestsExecutedAt(LocalDateTime.now())
                    .withTestRun(testRunNumber).asComment();

            if (!dryRun()) {
                issueTracker.addComment(issueId, testResultComment.asText());
            }
        } else {
            testResultComment = TestResultComment.fromText(existingComment.get().getBody())
                    .withWikiRendering(isWikiRenderedActive())
                    .withUpdatedTestResults(newTestResults)
                    .withUpdatedReportUrl(linkToReport(testOutcomes))
                    .forTestsExecutedAt(LocalDateTime.now())
                    .withUpdatedTestRunNumber(testRunNumber);

            IssueComment updatedComment = existingComment.get().withText(testResultComment.asText());
            if (!dryRun()) {
                issueTracker.updateComment(issueId, updatedComment);
            }
        }
        return testResultComment;
    }

    private boolean createNewCommentForEachUpdate() {
        return environmentVariables.getPropertyAsBoolean(ALWAYS_CREATE_NEW_COMMENT, false);
    }

    private Optional<IssueComment> findExistingSerenityCommentIn(List<IssueComment> comments) {
        return comments.stream()
                .filter(comment -> comment.getBody().contains(SERENITY_COMMENT_HEADING))
                .findFirst();
    }

    private void logIssueTracking(final String issueId) {
        if (dryRun()) {
            LOGGER.info("--- DRY RUN ONLY: JIRA WILL NOT BE UPDATED ---");
        }
        LOGGER.info("Updating JIRA issue: " + issueId);
        LOGGER.info("JIRA server: " + issueTracker.toString());
    }

    private boolean dryRun() {
        return Boolean.parseBoolean(environmentVariables.getProperty(SKIP_JIRA_UPDATES));
    }

    private String linkToReport(List<TestOutcomeSummary> testOutcomes) {
        TestOutcomeSummary firstTestOutcome = testOutcomes.get(0);
        String reportUrl = ThucydidesSystemProperty.SERENITY_PUBLIC_URL.from(environmentVariables, "");
        String reportName = firstTestOutcome.getReportName() + ".html";
        return formatTestResultsLink(reportUrl, reportName);
    }

    private String formatTestResultsLink(String reportUrl, String reportName) {
        return reportUrl + "/" + reportName;
    }

    private boolean isWikiRenderedActive() {
        return configuration.isWikiRenderedActive();
    }

    public List<String> getPrefixedIssuesWithoutHashes(TestOutcomeSummary result) {
        return addPrefixesIfRequired(stripInitialHashesFrom(issueReferencesIn(result)));
    }

    private List<String> addPrefixesIfRequired(final List<String> issueNumbers) {
        return issueNumbers.stream().map(this::toIssueNumbersWithPrefixes).collect(Collectors.toList());
    }

    private List<String> issueReferencesIn(TestOutcomeSummary result) {
        return result.getIssues();
    }

    private String toIssueNumbersWithPrefixes(String issueNumber) {
        if (StringUtils.isEmpty(projectPrefix)) {
            return issueNumber;
        }
        if (issueNumber.startsWith(projectPrefix)) {
            return issueNumber;
        }
        return projectPrefix + "-" + issueNumber;
    }

    private List<String> stripInitialHashesFrom(final List<String> issueNumbers) {
        return issueNumbers.stream().map(this::toIssueNumbersWithoutHashes).collect(Collectors.toList());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    private String toIssueNumbersWithoutHashes(String issueNumber) {
        if (issueNumber.startsWith("#")) {
            return issueNumber.substring(1);
        } else {
            return issueNumber;
        }
    }

    private int getMaxJobs() {
        return environmentVariables.getPropertyAsInteger("jira.max.threads", DEFAULT_MAX_THREADS);
    }

    protected Workflow getWorkflow() {
        return workflow;
    }

    protected boolean shouldUpdateWorkflow() {
        return Boolean.valueOf(environmentVariables.getProperty(ClasspathWorkflowLoader.ACTIVATE_WORKFLOW_PROPERTY));
    }

    public IssueTracker getIssueTracker() {
        return issueTracker;
    }

}
