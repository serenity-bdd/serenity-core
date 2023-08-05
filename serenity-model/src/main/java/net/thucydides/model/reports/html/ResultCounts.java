package net.thucydides.model.reports.html;

import net.thucydides.model.domain.TestResult;
import net.thucydides.model.domain.TestType;
import net.thucydides.model.reports.TestOutcomes;

import java.util.*;
import java.util.stream.Collectors;

public class ResultCounts {
    private TestOutcomes testOutcomes;

    private final Map<TestResult, Long> automatedTests = new HashMap<>();
    private final Map<TestResult, Long> manualTests = new HashMap<>();
    private final Map<TestResult, Long> totalTests = new HashMap<>();
    private final long totalAutomatedTests;
    private final long totalManualTests;
    private final long totalTestCount;

    public ResultCounts(TestOutcomes testOutcomes) {
        this.testOutcomes = testOutcomes;
        for (TestResult result : TestResult.values()) {
            automatedTests.put(result, testOutcomes.countWithResult(TestType.AUTOMATED, result));
            manualTests.put(result, testOutcomes.countWithResult(TestType.MANUAL, result));
            totalTests.put(result, automatedTests.get(result) + manualTests.get(result));
        }
        this.totalAutomatedTests = testOutcomes.countWithType(TestType.AUTOMATED);
        this.totalManualTests = testOutcomes.countWithType(TestType.MANUAL);
        this.totalTestCount = testOutcomes.getTotal();
    }

    public boolean hasManualTests() {
        return manualTests.values().stream().anyMatch(value -> value > 0);
    }

    public Long getAutomatedTestCount(String result) {
        return automatedTests.getOrDefault(TestResult.valueOf(result.toUpperCase()), 0L);
    }

    public Long getManualTestCount(String result) {
        return manualTests.getOrDefault(TestResult.valueOf(result.toUpperCase()), 0L);
    }

    public Long getOverallTestCount(String result) {
        return totalTests.getOrDefault(TestResult.valueOf(result.toUpperCase()), 0L);
    }

    public Integer getOverallTestsCount(String... results) {
        int allTestsCount = 0;
        for (String result : results) {
            allTestsCount += getOverallTestCount(result);
        }
        return allTestsCount;
    }

    public Long getTotalAutomatedTestCount() {
        return totalAutomatedTests;
    }

    public Long getTotalManualTestCount() {
        return totalManualTests;
    }

    public Long getTotalOverallTestCount() {
        return totalTestCount;
    }

    public String getAutomatedTestPercentageLabel(String result) {
        return percentageLabelFor(getAutomatedTestPercentage(result));
    }

    public String getManualTestPercentageLabel(String result) {
        return percentageLabelFor(getManualTestPercentage(result));
    }

    public String getOverallTestPercentageLabel(String result) {
        return percentageLabelFor(getOverallTestPercentage(result));
    }

    public Integer getAutomatedTestPercentage(String result) {
        return (int) Math.round(getAutomatedTestCount(result) * 100.0 / totalTestCount);
    }

    public Integer getManualTestPercentage(String result) {
        return (int) Math.round(getManualTestCount(result) * 100.0 / totalTestCount);
    }

    public Integer getOverallTestPercentage(String result) {
        return (int) Math.round(getOverallTestCount(result) * 100.0 / totalTestCount);
    }

    public Double getPreciseTestPercentage(String result) {
        return getOverallTestCount(result) * 100.0 / totalTestCount;
    }

    public static ResultCounts forOutcomesIn(TestOutcomes testOutcomes) {
        return new ResultCounts(testOutcomes);
    }

    /**
     * Returns automated and manual result counts of each of the specified result types
     */
    public String byTypeFor(String... testResultTypes) {
        List<String> resultCounts = new ArrayList<>();
        for (String resultType : testResultTypes) {
            resultCounts.add(labeledValue(resultType, TestType.ANY));
        }
        return Arrays.toString(resultCounts.toArray());
    }

    public String allResultValuesFor(String... testResultTypes) {
        return "[" + Arrays.stream(testResultTypes)
                .map(this::getOverallTestCount)
                .map(value -> Long.toString(value))
                .collect(Collectors.joining(",")) + "]";
    }

    public String automatedResultValuesFor(String... testResultTypes) {
        return "[" + Arrays.stream(testResultTypes)
                .map(this::getAutomatedTestCount)
                .map(value -> Long.toString(value))
                .collect(Collectors.joining(",")) + "]";
    }

    public String manualResultValuesFor(String... testResultTypes) {
        return "[" + Arrays.stream(testResultTypes)
                .map(this::getManualTestCount)
                .map(value -> Long.toString(value))
                .collect(Collectors.joining(",")) + "]";
    }

    private String labeledValue(String resultType, TestType testType) {
        long resultCount;
        if (testType == TestType.AUTOMATED) {
            resultCount = this.getAutomatedTestCount(resultType);
        } else if (testType == TestType.MANUAL) {
            resultCount = this.getManualTestCount(resultType);
        } else {
            resultCount = this.getOverallTestCount(resultType);
        }
        String label = TestResult.valueOf(resultType.toUpperCase()).getLabel();
        if (testType != TestType.ANY) {
            label = label + " (" + testType.toString().toLowerCase() + ")";
        }
        return "{meta: '" + label + "', value: " + resultCount + "}";
    }

    public String percentageLabelsByTypeFor(String... testResultTypes) {
        List<String> resultLabels = new ArrayList<>();
        long totalTestCount = testOutcomes.getTestCaseCount();
        for (String resultType : testResultTypes) {
            double percentageAutomated = automatedTests.get(TestResult.valueOf(resultType.toUpperCase())) * 100.0 / totalTestCount;
            double percentageManual = manualTests.get(TestResult.valueOf(resultType.toUpperCase())) * 100.0 / totalTestCount;

            resultLabels.add("'" + percentageLabelFor(percentageAutomated) + "'");
            resultLabels.add("'" + percentageLabelFor(percentageManual) + "'");
        }
        return Arrays.toString(resultLabels.toArray());
    }

    private String percentageLabelFor(double value) {
        return (value > 0.0) ? Math.round(value) + "%" : " ";
    }


}
