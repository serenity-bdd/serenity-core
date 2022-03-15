package net.thucydides.core.reports.html;

import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestType;
import net.thucydides.core.reports.TestOutcomes;

import java.util.*;
import java.util.stream.Collectors;

public class ResultCounts {
    private TestOutcomes testOutcomes;

    private final Map<TestResult, Integer> automatedTests = new HashMap<>();
    private final Map<TestResult, Integer> manualTests = new HashMap<>();
    private final Map<TestResult, Integer> totalTests = new HashMap<>();
    private final int totalAutomatedTests;
    private final int totalManualTests;
    private final int totalTestCount;

    public ResultCounts(TestOutcomes testOutcomes) {
        this.testOutcomes = testOutcomes;
        for (TestResult result : TestResult.values()) {
            automatedTests.put(result, testOutcomes.ofType(TestType.AUTOMATED).scenarioCountWithResult(result));
            manualTests.put(result, testOutcomes.ofType(TestType.MANUAL).scenarioCountWithResult(result));
            totalTests.put(result, automatedTests.get(result) + manualTests.get(result));
        }
        this.totalAutomatedTests = testOutcomes.ofType(TestType.AUTOMATED).getTotal();
        this.totalManualTests = testOutcomes.ofType(TestType.MANUAL).getTotal();
        this.totalTestCount = testOutcomes.getTotal();
    }

    public boolean hasManualTests() {
        return manualTests.values().stream().anyMatch(value -> value > 0);
    }

    public Integer getAutomatedTestCount(String result) {
        return automatedTests.getOrDefault(TestResult.valueOf(result.toUpperCase()), 0);
    }

    public Integer getManualTestCount(String result) {
        return manualTests.getOrDefault(TestResult.valueOf(result.toUpperCase()), 0);
    }

    public Integer getOverallTestCount(String result) {
        return totalTests.getOrDefault(TestResult.valueOf(result.toUpperCase()), 0);
    }

    public Integer getOverallTestsCount(String... results) {
        int allTestsCount = 0;
        for (String result : results) {
            allTestsCount += getOverallTestCount(result);
        }
        return allTestsCount;
    }

    public Integer getTotalAutomatedTestCount() {
        return totalAutomatedTests;
    }

    public Integer getTotalManualTestCount() {
        return totalManualTests;
    }

    public Integer getTotalOverallTestCount() {
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
//            resultCounts.add(labeledValue(resultType, TestType.AUTOMATED));
//            resultCounts.add(labeledValue(resultType, TestType.MANUAL));
        }
        return Arrays.toString(resultCounts.toArray());
    }

    public String allResultValuesFor(String... testResultTypes) {
        return "[" + Arrays.stream(testResultTypes)
                .map(this::getOverallTestCount)
                .map(value -> Integer.toString(value))
                .collect(Collectors.joining(",")) + "]";
    }

    public String automatedResultValuesFor(String... testResultTypes) {
        return "[" + Arrays.stream(testResultTypes)
                .map(this::getAutomatedTestCount)
                .map(value -> Integer.toString(value))
                .collect(Collectors.joining(",")) + "]";
    }

    public String manualResultValuesFor(String... testResultTypes) {
        return "[" + Arrays.stream(testResultTypes)
                .map(this::getManualTestCount)
                .map(value -> Integer.toString(value))
                .collect(Collectors.joining(",")) + "]";
    }

    private String labeledValue(String resultType, TestType testType) {
        int resultCount;
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
        int totalTestCount = testOutcomes.getTestCount();
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
