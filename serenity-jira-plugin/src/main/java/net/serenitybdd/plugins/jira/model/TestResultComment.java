package net.serenitybdd.plugins.jira.model;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestResultList;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


public class TestResultComment {

    private final String testRunNumber;
    private final SortedMap<String, NamedTestResult> namedTestResults;
    private final String reportUrl;
    private final boolean wikiRenderingActive;
    private final LocalDateTime executionTime;

    private final static int REPORT_URL_LINE = 1;
    private final static int TEXT_NUMBER_LINE = 2;
    private final static int FIRST_TEST_RESULT_LINE = 3;

    protected TestResultComment(String commentText) {
        List<String> commentLines = ImmutableList.copyOf(commentText.split("\\r?\\n"));
        reportUrl = findReportUrl(commentLines);
        testRunNumber = findTestRunNumber(commentLines);
        namedTestResults = findTestResults(commentLines);
        wikiRenderingActive = true;
        executionTime = LocalDateTime.now();

    }

    protected TestResultComment(String reportUrl,
                                String testRunNumber,
                                List<NamedTestResult> namedTestResults,
                                boolean wikiRenderingActive,
                                LocalDateTime executionTime) {
        this.reportUrl = reportUrl;
        this.testRunNumber = testRunNumber;
        this.namedTestResults = indexByTestName(namedTestResults);
        this.wikiRenderingActive = wikiRenderingActive;
        this.executionTime = executionTime;
    }

    public static JIRACommentBuilder comment(boolean wikiRenderingActive) {
        return new JIRACommentBuilder(wikiRenderingActive);
    }

    public static TestResultComment fromText(String commentText) {
        return new TestResultComment(commentText);
    }

    private SortedMap<String, NamedTestResult> findTestResults(List<String> commentLines) {
        List<String> testResultLines = linesStartingAtRowIn(commentLines, FIRST_TEST_RESULT_LINE);
        List<NamedTestResult> namedTestResults = testResultLines.stream().map(this::toNamedTestResults).collect(Collectors.toList());
        return indexByTestName(namedTestResults);
    }

    private SortedMap<String, NamedTestResult> indexByTestName(List<NamedTestResult> namedTestResults) {
        Map<String, NamedTestResult> indexedTestResults = namedTestResults.stream().collect(Collectors.toMap(NamedTestResult::getTestName, Function.identity()));
        SortedMap<String, NamedTestResult> sortedTestResults = Maps.newTreeMap();
        sortedTestResults.putAll(indexedTestResults);
        return sortedTestResults;
    }

    private List<String> linesStartingAtRowIn(List<String> commentLines, int startingIndex) {
        if (commentLines.size() >= startingIndex) {
            return commentLines.subList(startingIndex, commentLines.size());
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    private NamedTestResult toNamedTestResults(String commentLine) {
        String testName = stripInitialDash(textBeforeColon(commentLine));
        TestResult result = getTestResult(commentLine);
        return new NamedTestResult(testName, result);
    }

    private TestResult getTestResult(String commentLine) {
        try {
            return TestResult.valueOf(textAfterColon(commentLine));
        } catch( IllegalArgumentException e) {
            return TestResult.UNDEFINED;
        }
    }

    private String stripInitialDash(String testResultText) {
        if (testResultText.trim().startsWith("-")) {
            return testResultText.trim().substring(2);
        } else {
            return testResultText.trim();
        }
    }

    private String findTestRunNumber(List<String> commentLines) {
        if (commentLines.size() > TEXT_NUMBER_LINE) {
            return textAfterColon(commentLines.get(TEXT_NUMBER_LINE));
        } else {
            return null;
        }
    }

    private String findReportUrl(List<String> commentLines) {
        if (commentLines.size() > REPORT_URL_LINE) {
            return reportUrlIn(commentLines.get(REPORT_URL_LINE));
        } else {
            return null;
        }
    }

    private String reportUrlIn(String commentLine) {
        if (wikiFormatUrl(commentLine)) {
            return wikiFormattedUrl(commentLine);
        } else {
            return textAfterColon(commentLine);
        }
    }

    private String wikiFormattedUrl(String commentLine) {
        //[Test report|http://my.server/myproject/thucydides/my_test.html]
        int pipe = commentLine.indexOf("|");
        int endBracket = commentLine.indexOf("]");
        return commentLine.substring(pipe + 1, endBracket);
    }

    private boolean wikiFormatUrl(String commentLine) {
        return commentLine.contains("[");
    }

    public String getReportUrl() {
        return reportUrl;
    }

    private String textBeforeColon(String line) {
        String[] lineTokens = splitAtColon(line);
        return lineTokens[0].trim();
    }

    private String[] splitAtColon(String line) {
        return line.split(":", 3);
    }


    private String textAfterColon(String line) {
        String[] lineTokens = splitAtColon(line);
        if (lineTokens.length >= 2) {
            return splitAtColon(line)[1].trim();
        } else {
            return null;
        }
    }

    public String getTestRunNumber() {
        return testRunNumber;
    }

    public List<NamedTestResult> getNamedTestResults() {
        if (namedTestResults.isEmpty()) {
            return Lists.newArrayList();
        } else {
            return new ArrayList<>(namedTestResults.values());
        }
    }


    public TestResult getOverallResult() {
        return TestResultList.overallResultFrom(namedTestResults.values().stream().map(NamedTestResult::getTestResult).collect(Collectors.toList()));
    }

    public String asText() {
        return toString();
    }

    public String toString() {
        return comment(wikiRenderingActive).withTestRun(testRunNumber)
                .withReportUrl(reportUrl)
                .withNamedResults(getNamedTestResults())
                .asText();
    }

    public TestResultComment withUpdatedTestResults(final List<NamedTestResult> newTestResults) {
        Map<String, NamedTestResult> mergedTestResultsIndexedByName = Maps.newHashMap();
        mergedTestResultsIndexedByName.putAll(namedTestResults);

        for (NamedTestResult testResult : newTestResults) {
            mergedTestResultsIndexedByName.put(testResult.getTestName(), testResult);
        }

        List<NamedTestResult> mergedTestResults = Lists.newArrayList();
        mergedTestResults.addAll(mergedTestResultsIndexedByName.values());

        return comment(wikiRenderingActive).withTestRun(testRunNumber)
                .withReportUrl(reportUrl)
                .withNamedResults(mergedTestResults).asComment();
    }

    public TestResultComment withUpdatedReportUrl(String newReportUrl) {
        return new TestResultComment(newReportUrl, this.testRunNumber, getNamedTestResults(),wikiRenderingActive, executionTime);
    }

    public TestResultComment withUpdatedTestRunNumber(String newTestRunNumber) {
        return new TestResultComment(this.reportUrl, newTestRunNumber, getNamedTestResults(),wikiRenderingActive, executionTime);
    }

    public TestResultComment withWikiRendering(boolean isWikiRenderedActive) {
        return new TestResultComment(this.reportUrl, this.testRunNumber, getNamedTestResults(), isWikiRenderedActive, executionTime);
    }

    public TestResultComment forTestsExecutedAt(LocalDateTime executionTime) {
        return new TestResultComment(this.reportUrl, this.testRunNumber, getNamedTestResults(), wikiRenderingActive, executionTime);
    }

}
