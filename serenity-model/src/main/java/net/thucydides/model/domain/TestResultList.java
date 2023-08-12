package net.thucydides.model.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * A list of test results, used to determine the overall test result.
 */
public class TestResultList {

    public static TestResult overallResultFrom(List<TestResult> results) {
        List<TestResult> testResults = withoutResultsUndefined(new ArrayList<>(results));

        if (testResults.isEmpty()) {
            return TestResult.SUCCESS;
        }

        if (testResults.contains(TestResult.COMPROMISED)) {
            return TestResult.COMPROMISED;
        }

        if (testResults.contains(TestResult.ERROR)) {
            return TestResult.ERROR;
        }

        if (testResults.contains(TestResult.FAILURE)) {
            return TestResult.FAILURE;
        }

        if (testResults.contains(TestResult.PENDING)) {
            return TestResult.PENDING;
        }

        if (testResults.contains(TestResult.IGNORED)) {
            return TestResult.IGNORED;
        }

        if (containsOnly(testResults, TestResult.SKIPPED)) {
            return TestResult.SKIPPED;
        }

        if (containsOnly(testResults, TestResult.SUCCESS, TestResult.IGNORED, TestResult.SKIPPED)) {
            return TestResult.SUCCESS;
        }
        return TestResult.SUCCESS;
    }

    private static List<TestResult> withoutResultsUndefined(List<TestResult> testResults) {
        List<TestResult> filteredResults = new ArrayList<>();
        for(TestResult result : testResults) {
            if (result != TestResult.UNDEFINED) {
                filteredResults.add(result);
            }
        }
        return filteredResults;
    }

    private static boolean containsOnly(final List<TestResult> testResults, final TestResult... values) {
        for (TestResult result : testResults) {
            if (!AuthorisedResults.allows(values, result)) {
                return false;
            }
        }
        return true;
    }

    private static class AuthorisedResults {

        public static boolean allows(TestResult[] allowedValues, TestResult result) {
            for(TestResult allowedValue : allowedValues) {
                if (result == allowedValue) {
                    return true;
                }
            }
            return false;
        }
    }
}
