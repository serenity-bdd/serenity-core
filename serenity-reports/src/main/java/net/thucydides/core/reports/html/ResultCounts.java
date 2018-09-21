package net.thucydides.core.reports.html;

import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestType;
import net.thucydides.core.reports.TestOutcomes;

import java.util.*;

import static java.util.Arrays.*;
import static net.thucydides.core.model.TestResult.*;

public class ResultCounts {
    private TestOutcomes testOutcomes;

    private static final List<TestResult> ORDERED_TEST_RESULTS
            = asList(SUCCESS, PENDING, IGNORED, SKIPPED, FAILURE, ERROR, COMPROMISED);

    private final Map<TestResult, Integer> automatedTests = new HashMap<>();
    private final Map<TestResult, Integer> manualTests = new HashMap<>();
    private final Map<TestResult, Integer> totalTests = new HashMap<>();
    private final int totalTestCount;

    public ResultCounts(TestOutcomes testOutcomes) {
        this.testOutcomes = testOutcomes;
        for(TestResult result : TestResult.values()) {
            automatedTests.put(result, testOutcomes.ofType(TestType.AUTOMATED).withResult(result).getTotal());
            manualTests.put(result, testOutcomes.ofType(TestType.MANUAL).withResult(result).getTotal());
            totalTests.put(result, automatedTests.get(result) + manualTests.get(result));
        }
        this.totalTestCount = testOutcomes.getTestCount();
    }

    public boolean hasManualTests() {
        return manualTests.values().stream().anyMatch(value -> value > 0);
    }

    public Integer getAutomatedTestCount(String result) {
        return automatedTests.getOrDefault(TestResult.valueOf(result.toUpperCase()),0);
    }

    public Integer getManualTestCount(String result) {
        return manualTests.getOrDefault(TestResult.valueOf(result.toUpperCase()),0);
    }

    public Integer getOverallTestCount(String result) {
        return totalTests.getOrDefault(TestResult.valueOf(result.toUpperCase()),0);
    }

    public String getAutomatedTestPercentage(String result) {
        return percentageLabelFor(getAutomatedTestCount(result) * 100.0 / totalTestCount);
    }

    public String getManualTestPercentage(String result) {
        return percentageLabelFor(getManualTestCount(result) * 100.0 / totalTestCount);
    }

    public String getOverallTestPercentage(String result) {
        return percentageLabelFor(getOverallTestCount(result) * 100.0 / totalTestCount);
    }

    public static ResultCounts forOutcomesIn(TestOutcomes testOutcomes) {
        return new ResultCounts(testOutcomes);
    }

    /**
     * Returns automated and manual result counts of each of the specified result types
     */
    public String byTypeFor(String... testResultTypes) {
        List<Integer> resultCounts = new ArrayList<>();
        for(String resultType : testResultTypes) {
            resultCounts.add(testOutcomes.ofType(TestType.AUTOMATED).withResult(TestResult.valueOf(resultType.toUpperCase())).getTotal());
            resultCounts.add(testOutcomes.ofType(TestType.MANUAL).withResult(TestResult.valueOf(resultType.toUpperCase())).getTotal());
        }
        return Arrays.toString(resultCounts.toArray());
    }

    public String percentageLabelsByTypeFor(String... testResultTypes) {
        List<String> resultLabels = new ArrayList<>();
        int totalTestCount = testOutcomes.getTestCount();
        for(String resultType : testResultTypes) {
            double percentageAutomated = testOutcomes.ofType(TestType.AUTOMATED).withResult(TestResult.valueOf(resultType.toUpperCase())).getTotal() * 100.0 / totalTestCount;
            double percentageManual = testOutcomes.ofType(TestType.MANUAL).withResult(TestResult.valueOf(resultType.toUpperCase())).getTotal() * 100.0 / totalTestCount;

            resultLabels.add("'" + percentageLabelFor(percentageAutomated) + "'");
            resultLabels.add("'" + percentageLabelFor(percentageManual) + "'");
        }
        return Arrays.toString(resultLabels.toArray());
    }

    private String percentageLabelFor(double value) {
        return (value > 0.0) ? Math.round(value) + "%" : " ";
    }


}
