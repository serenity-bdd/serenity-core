package net.thucydides.core.reports.html;

import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestType;
import net.thucydides.core.requirements.reports.ScenarioOutcome;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ScenarioResultCounts {
    private final List<ScenarioOutcome> scenarios;

    public ScenarioResultCounts(List<ScenarioOutcome> scenarios) {
        this.scenarios = scenarios;
    }

    public static ScenarioResultCounts forScenarios(List<ScenarioOutcome> scenarios) {
        List<ScenarioOutcome> mainScenarios = scenarios.stream().filter(
                scenarioOutcome -> !scenarioOutcome.isBackground()
        ).collect(Collectors.toList());
        return new ScenarioResultCounts(mainScenarios);
    }

    /**
     * Returns automated and manual result counts of each of the specified result types
     */
    public String byTypeFor(String... testResultTypes) {
        List<String> resultCounts = new ArrayList<>();
        for (String resultType : testResultTypes) {
            resultCounts.add(labeledValue(resultType));
        }
        return Arrays.toString(resultCounts.toArray());
    }

    private String labeledValue(String resultType) {
        long resultCount;
        TestResult expectedResult = TestResult.valueOf(resultType.toUpperCase());
        resultCount = getOverallTestCount(expectedResult);
        String label = TestResult.valueOf(resultType.toUpperCase()).getLabel();
        return "{meta: '" + label + "', value: " + resultCount + "}";
    }

    public long getOverallTestCount(TestResult expectedResult) {
        return scenarios.stream()
                .filter(scenarioOutcome -> scenarioOutcome.getResult() == expectedResult)
                .count();
    }

    public String percentageLabelsByTypeFor(String... testResultTypes) {
        List<String> resultLabels = new ArrayList<>();
        int totalTestCount = scenarios.size();
        for (String resultType : testResultTypes) {
            TestResult expectedResult = TestResult.valueOf(resultType.toUpperCase());
            double percentageResult = getOverallTestPercentage(expectedResult) * 100 / totalTestCount;
            resultLabels.add("'" + percentageLabelFor(percentageResult) + "'");
        }
        return Arrays.toString(resultLabels.toArray());
    }

    private String percentageLabelFor(double value) {
        return (value > 0.0) ? Math.round(value) + "%" : " ";
    }

    public String getOverallTestPercentageLabel(String result) {
        TestResult expectedResult = TestResult.valueOf(result.toUpperCase());
        return percentageLabelFor(getOverallTestPercentage(expectedResult));
    }

    public Integer getOverallTestPercentage(TestResult expectedResult) {
        return (int) Math.round(getOverallTestCount(expectedResult) * 100.0 / scenarios.size());
    }
}
