package net.thucydides.model.domain;

import net.serenitybdd.model.collect.NewList;

import java.util.List;

public class HeuristicTestResult {

    private final static List<String> ASSERTION_HINTS = NewList.of("Assert", "Failure", "Failed");

    public static TestResult from(String testFailureClassname) {
        return (looksLikAnAssertion(testFailureClassname)) ?  TestResult.FAILURE : TestResult.ERROR;
    }

    private static boolean looksLikAnAssertion(String testFailureClassname) {
        return ASSERTION_HINTS.stream()
                .anyMatch(testFailureClassname::contains);
    }
}
