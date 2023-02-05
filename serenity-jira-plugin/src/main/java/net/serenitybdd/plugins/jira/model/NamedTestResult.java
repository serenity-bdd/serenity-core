package net.serenitybdd.plugins.jira.model;

import net.thucydides.core.model.TestResult;

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
