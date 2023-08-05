package net.serenitybdd.plugins.jira.model;


import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.domain.TestResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;


public class JIRACommentBuilder {
    public static final String SERENITY_COMMENT_HEADING = "Serenity BDD Automated Acceptance Tests";
    private final boolean wikiRendering;
    private final String testRunNumber;
    private final String reportUrl;
    private final LocalDateTime executionTime;
    private final List<NamedTestResult> namedTestResults;

    private final static String NEW_LINE = System.getProperty("line.separator");

    public JIRACommentBuilder(boolean wikiRendering) {
        this(wikiRendering, null);
    }

    public JIRACommentBuilder(final boolean wikiRendering, final String reportUrl) {
        this(wikiRendering, reportUrl, null, null, LocalDateTime.now());
    }

    public JIRACommentBuilder(final boolean wikiRendering,
                              final String reportUrl,
                              final List<NamedTestResult> testOutcomes) {
        this(wikiRendering, reportUrl, testOutcomes,null, LocalDateTime.now());
    }


    public JIRACommentBuilder(final boolean wikiRendering,
                              final List<NamedTestResult> testOutcomes,
                              final String reportUrl,
                              final String testRunNumber) {
        this(wikiRendering, reportUrl, testOutcomes, testRunNumber, LocalDateTime.now());
    }


    private static List<NamedTestResult> namedTestResultsFrom(List<TestOutcome> testOutcomes) {
        return testOutcomes.stream().map(testOutcome -> new NamedTestResult(testOutcome.getTitle(), testOutcome.getResult())).collect(Collectors.toList());
    }

    public JIRACommentBuilder(boolean wikiRendering,
                              String reportUrl,
                              List<NamedTestResult> namedTestResults,
                              String testRunNumber,
                              LocalDateTime executionTime) {
        this.reportUrl = reportUrl;
        this.namedTestResults = namedTestResults;
        this.testRunNumber = testRunNumber;
        this.wikiRendering = wikiRendering;
        this.executionTime = executionTime;
    }


    public String asText() {
        StringBuilder commentBuilder = new StringBuilder();
        addLine(commentBuilder, bold(SERENITY_COMMENT_HEADING));

        String executionTimestamp = "Tests run: "
                + executionTime.format(DateTimeFormatter.ofPattern("d MMM yyyy hh:mm a"));

        if (wikiRendering) {
            addLine(commentBuilder, executionTimestamp + " - [full report|" + reportUrl + "]");
        } else {
            addLine(commentBuilder, executionTimestamp + " - full report: " + reportUrl);
        }
        if (testRunNumber != null) {
            addLine(commentBuilder, "Test Run: " + testRunNumber);
        }
        addLineForEachTest(commentBuilder);
        return commentBuilder.toString();
    }

    private String bold(String text) {
        return (wikiRendering) ? "*" + text + "*" : text;
    }

    private void addLineForEachTest(StringBuilder commentBuilder) {
        if (namedTestResults != null) {
            for (NamedTestResult testResult : namedTestResults) {

                addLine(commentBuilder, "  - " + testResult.getTestName() + ": " + testResult.getTestResult() + " "
                + resultIconFor(testResult.getTestResult()));
            }
        }
    }

    private String resultIconFor(TestResult testResult) {
        if (!wikiRendering) {
            return "";
        }

        switch (testResult) {
            case SUCCESS: return ": (/)";
            case FAILURE: return ": (x)";
            case ERROR: return ": (x)";
            case PENDING: return ": (!)";
            case SKIPPED: return ": (!)";
            case IGNORED: return ": (!)";
            default: return ": (?)";
        }
    }

    private void addLine(StringBuilder commentBuilder, final String line) {
        commentBuilder.append(line).append(NEW_LINE);
    }

    public JIRACommentBuilder withResults(final List<NamedTestResult> testOutcomes) {
        return new JIRACommentBuilder(this.wikiRendering, reportUrl, testOutcomes);
    }

    public JIRACommentBuilder withTestRun(final String testRunNumber) {
        return new JIRACommentBuilder(this.wikiRendering, this.reportUrl, this.namedTestResults, testRunNumber, executionTime);
    }

    public JIRACommentBuilder withReportUrl(final String reportUrl) {
        return new JIRACommentBuilder(this.wikiRendering, reportUrl, this.namedTestResults, this.testRunNumber, executionTime);
    }

    public JIRACommentBuilder withNamedResults(List<NamedTestResult> namedTestResults) {
        return new JIRACommentBuilder(this.wikiRendering, this.reportUrl, namedTestResults, this.testRunNumber, executionTime);
    }

    public JIRACommentBuilder forTestsExecutedAt(LocalDateTime executionTime) {
        return new JIRACommentBuilder(this.wikiRendering, this.reportUrl, namedTestResults, this.testRunNumber, executionTime);
    }

    public TestResultComment asComment() {
        return new TestResultComment(reportUrl, testRunNumber, namedTestResults, wikiRendering, executionTime);
    }
}
