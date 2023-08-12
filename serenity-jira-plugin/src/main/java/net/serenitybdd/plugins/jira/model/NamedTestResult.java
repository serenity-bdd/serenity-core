package net.serenitybdd.plugins.jira.model;

import net.thucydides.model.domain.TestResult;

public class NamedTestResult {

    private final String testName;
    
    private final TestResult testResult;

    public NamedTestResult(String testName, TestResult testResult) {
        this.testName = testName;
        this.testResult = testResult;
    }

    public String getTestName() {
        return testName;
    }

    public TestResult getTestResult() {
        return testResult;
    }
}
