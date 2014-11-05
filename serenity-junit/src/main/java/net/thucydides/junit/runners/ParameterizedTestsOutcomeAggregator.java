package net.thucydides.junit.runners;

import com.google.common.collect.Lists;
import net.thucydides.core.model.*;
import org.apache.commons.lang3.StringUtils;
import org.junit.runner.Runner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

public class ParameterizedTestsOutcomeAggregator {
    private final ThucydidesParameterizedRunner thucydidesParameterizedRunner;

    private ParameterizedTestsOutcomeAggregator(ThucydidesParameterizedRunner thucydidesParameterizedRunner) {
        this.thucydidesParameterizedRunner = thucydidesParameterizedRunner;
    }

    public static ParameterizedTestsOutcomeAggregator from(ThucydidesParameterizedRunner thucydidesParameterizedRunner) {
        return new ParameterizedTestsOutcomeAggregator(thucydidesParameterizedRunner);
    }


    public List<TestOutcome> aggregateTestOutcomesByTestMethods() {
        List<TestOutcome> allOutcomes = getTestOutcomesForAllParameterSets();

        if (allOutcomes.isEmpty()) {
            return Lists.<TestOutcome>newArrayList();
        } else {
            return aggregatedScenarioOutcomes(allOutcomes);
        }

    }

    private List<TestOutcome> aggregatedScenarioOutcomes(List<TestOutcome> allOutcomes) {
        Map<String, TestOutcome> scenarioOutcomes = new HashMap<>();

        for (TestOutcome testOutcome : allOutcomes) {
            String normalizedMethodName = normalizeMethodName(testOutcome);

            if (scenarioOutcomes.containsKey(normalizedMethodName)) {
                List<TestStep> testSteps = testOutcome.getTestSteps();
                if (testSteps.isEmpty()) {
                    TestStep nestedStep = TestStep.forStepCalled(testOutcome.getTitle()).withResult(testOutcome.getResult());
                    scenarioOutcomes.get(normalizedMethodName).recordStep(nestedStep);
                } else {
                    TestStep nextStep = testSteps.get(0);
                    nextStep.setDescription(normalizeTestStepDescription(nextStep.getDescription(), scenarioOutcomes.get(normalizedMethodName).getTestSteps().size() + 1));
                    scenarioOutcomes.get(normalizedMethodName).recordStep(nextStep);
                }
                if (testOutcome.isDataDriven()) {
                    updateResultsForAnyExternalFailures(scenarioOutcomes.get(normalizedMethodName), testOutcome);
                    scenarioOutcomes.get(normalizedMethodName).getDataTable().addRows(testOutcome.getDataTable().getRows());
                }

            }
            else {
                TestOutcome scenarioOutcome = createScenarioOutcome(testOutcome);
                if (scenarioOutcome.getTestSteps().isEmpty()) {
                    scenarioOutcome.startGroup();
                }
                scenarioOutcomes.put(scenarioOutcome.getMethodName(), scenarioOutcome);
            }
        }

        List<TestOutcome> aggregatedScenarioOutcomes = new ArrayList<TestOutcome>();
        aggregatedScenarioOutcomes.addAll(scenarioOutcomes.values());
        return aggregatedScenarioOutcomes;

    }

    private void updateResultsForAnyExternalFailures(TestOutcome scenarioOutcome, TestOutcome testOutcome) {
        if (rowResultsAreInconsistantWithOverallResult(testOutcome)) {
            testOutcome.getDataTable().getRows().get(0).updateResult(testOutcome.getResult());
            scenarioOutcome.addFailingExternalStep(new AssertionError(testOutcome.getTestFailureMessage()));
        }
    }

    private boolean rowResultsAreInconsistantWithOverallResult(TestOutcome testOutcome) {
        TestResult overallRowResult = overallResultFrom(testOutcome.getDataTable().getRows());
        return (testOutcome.isError() || testOutcome.isFailure())
                && (!testOutcome.getDataTable().getRows().isEmpty())
                && (testOutcome.getResult() != overallRowResult);
    }

    private TestResult overallResultFrom(List<DataTableRow> rows) {
        TestResultList rowResults = TestResultList.of(extract(rows, on(DataTableRow.class).getResult()));
        return rowResults.getOverallResult();
    }

    private String normalizeTestStepDescription(String description, int index) {
        return StringUtils.replace(description, "[1]", "[" + index + "]");
    }

    private TestOutcome createScenarioOutcome(TestOutcome parameterizedOutcome) {
        TestOutcome scenarioOutcome = parameterizedOutcome.withMethodName(normalizeMethodName(parameterizedOutcome));
        if (scenarioOutcome.getTestSteps().isEmpty()) {
            TestStep firstStep = TestStep.forStepCalled(parameterizedOutcome.getTitle()).withResult(scenarioOutcome.getResult());
            scenarioOutcome.recordStep(firstStep);
        }
        scenarioOutcome.endGroup(); //pop group stack so next item gets added as sibling
        return scenarioOutcome;
    }

    private String normalizeMethodName(TestOutcome testOutcome) {
        return testOutcome.getMethodName().replaceAll("\\[\\d+\\]", "");
    }

    public List<TestOutcome> getTestOutcomesForAllParameterSets() {
        List<TestOutcome> testOutcomes = new ArrayList<TestOutcome>();

        for (Runner runner : thucydidesParameterizedRunner.getRunners()) {
            for (TestOutcome testOutcome : ((ThucydidesRunner) runner).getTestOutcomes()) {
                if (!testOutcomes.contains(testOutcome)) {
                    testOutcomes.add(testOutcome);
                }
            }
        }
        return testOutcomes;
    }
}