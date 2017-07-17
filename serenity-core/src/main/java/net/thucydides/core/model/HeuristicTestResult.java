package net.thucydides.core.model;

import com.google.common.collect.Lists;

import java.util.List;

public class HeuristicTestResult {

    private final static List<String> ASSERTION_HINTS = Lists.newArrayList("Assert", "Failure", "Failed");

    public static TestResult from(String testFailureClassname) {
        return (looksLikAnAssertion(testFailureClassname)) ?  TestResult.FAILURE : TestResult.ERROR;
    }

    private static boolean looksLikAnAssertion(String testFailureClassname) {
        return ASSERTION_HINTS.stream()
                .anyMatch(testFailureClassname::contains);
    }
}
