package net.thucydides.core.model;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

import static net.thucydides.core.model.TestResult.*;

/**
 * A list of test results, used to determine the overall test result.
 */
public class TestResultList {

    public static TestResult overallResultFrom(List<TestResult> results) {
        List<TestResult> testResults = withoutResultsUndefined(ImmutableList.copyOf(results));

        if (testResults.isEmpty()) {
            return SUCCESS;
        }

        if (testResults.contains(COMPROMISED)) {
            return COMPROMISED;
        }

        if (testResults.contains(ERROR)) {
            return ERROR;
        }

        if (testResults.contains(FAILURE)) {
            return FAILURE;
        }

        if (testResults.contains(PENDING)) {
            return PENDING;
        }

        if (containsOnly(testResults, IGNORED)) {
            return IGNORED;
        }

        if (containsOnly(testResults, SKIPPED)) {
            return SKIPPED;
        }

        if (containsOnly(testResults, SUCCESS, IGNORED, SKIPPED)) {
            return SUCCESS;
        }
        return SUCCESS;
    }

    private static List<TestResult> withoutResultsUndefined(List<TestResult> testResults) {
        List<TestResult> filteredResults = new ArrayList<>();
        for(TestResult result : testResults) {
            if (result != UNDEFINED) {
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
