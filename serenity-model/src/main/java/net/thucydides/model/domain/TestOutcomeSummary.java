package net.thucydides.model.domain;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Lightweight copy of a TestOutcome.
 */
public class TestOutcomeSummary {

    private List<String> issues = new ArrayList<>();
    private String title;
    private Path linkToTestReport;
    private TestResult testResult;
    private String reportName;
    private String name;

    public TestOutcomeSummary(TestOutcome testOutcome) {
        this.issues.addAll(testOutcome.getIssues());
        this.title = testOutcome.getTitle();
        this.testResult = testOutcome.getResult();
        this.reportName = testOutcome.getReportName();
        this.name = testOutcome.getName();
    }

    public TestOutcomeSummary(TestOutcome testOutcome, Path testReportLink) {
        this(testOutcome);
        this.linkToTestReport = testReportLink;
    }

    public List<String> getIssues()
    {
        return issues;
    }

    public Path getLinkToTestReport() {
        return linkToTestReport;
    }

    public String getTitle(){ return title;}

    public TestResult getTestResult()
    {
        return testResult;
    }

    public String getReportName(){
        return reportName;
    }

    public String getName() { return name; }
}
