package net.serenitybdd.junit.runners;

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
    private final SerenityParameterizedRunner serenityParameterizedRunner;

    private ParameterizedTestsOutcomeAggregator(SerenityParameterizedRunner serenityParameterizedRunner) {
        this.serenityParameterizedRunner = serenityParameterizedRunner;
    }

    public static ParameterizedTestsOutcomeAggregator from(SerenityParameterizedRunner serenityParameterizedRunner) {
        return new ParameterizedTestsOutcomeAggregator(serenityParameterizedRunner);
    }


    public List<TestOutcome> aggregateTestOutcomesByTestMethods() {
        List<TestOutcome> allOutcomes = getTestOutcomesForAllParameterSets();

        if (allOutcomes.isEmpty()) {
            return Lists.newArrayList();
        } else {
            return aggregatedScenarioOutcomes(allOutcomes);
        }

    }

    private List<TestOutcome> aggregatedScenarioOutcomes(List<TestOutcome> allOutcomes) {
        Map<String, TestOutcome> scenarioOutcomes = new HashMap<>();

        for (TestOutcome testOutcome : allOutcomes) {
            final String normalizedMethodName = normalizeMethodName(testOutcome);

            TestOutcome scenarioOutcome = scenarioOutcomeFor(normalizedMethodName, testOutcome, scenarioOutcomes);
            recordTestOutcomeAsSteps(testOutcome, scenarioOutcome);

            if (testOutcome.isDataDriven()) {
                updateResultsForAnyExternalFailures(testOutcome, scenarioOutcomes.get(normalizedMethodName));
                scenarioOutcome.addDataFrom(testOutcome.getDataTable());
            }

        }

        List<TestOutcome> aggregatedScenarioOutcomes = new ArrayList<>();
        aggregatedScenarioOutcomes.addAll(scenarioOutcomes.values());
        return aggregatedScenarioOutcomes;

    }

    private void recordTestOutcomeAsSteps(TestOutcome testOutcome, TestOutcome scenarioOutcome) {
        TestStep nestedStep = TestStep.forStepCalled(testOutcome.getTitle()).withResult(testOutcome.getResult());
        List<TestStep> testSteps = testOutcome.getTestSteps();

        if (testOutcome.getTestFailureCause() != null) {
            nestedStep.failedWith(testOutcome.getTestFailureCause().toException());
        }

        if (!testSteps.isEmpty()) {
            for (TestStep nextStep : testSteps) {
                nextStep.setDescription(normalizeTestStepDescription(nextStep.getDescription(),
                                        scenarioOutcome.getTestSteps().size() + 1));
                nestedStep.addChildStep(nextStep);
            }
        }
        scenarioOutcome.recordStep(nestedStep);
    }

    private TestOutcome scenarioOutcomeFor(String normalizedMethodName, TestOutcome testOutcome, Map<String, TestOutcome> scenarioOutcomes) {
        if (!scenarioOutcomes.containsKey(normalizedMethodName)) {
            TestOutcome scenarioOutcome = createScenarioOutcome(testOutcome);
            scenarioOutcomes.put(normalizedMethodName, scenarioOutcome);
        }
        return scenarioOutcomes.get(normalizedMethodName);
    }

    private void updateResultsForAnyExternalFailures(TestOutcome testOutcome, TestOutcome scenarioOutcome) {
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
        TestOutcome testOutcome = TestOutcome.forTest(normalizeMethodName(parameterizedOutcome),
                                                      parameterizedOutcome.getTestCase());

        return testOutcome;
    }

    private String normalizeMethodName(TestOutcome testOutcome) {
        return testOutcome.getName().replaceAll("\\[\\d+\\]", "");
    }

    public List<TestOutcome> getTestOutcomesForAllParameterSets() {
        List<TestOutcome> testOutcomes = new ArrayList<TestOutcome>();

        for (Runner runner : serenityParameterizedRunner.getRunners()) {
            for (TestOutcome testOutcome : ((SerenityRunner) runner).getTestOutcomes()) {
                if (!testOutcomes.contains(testOutcome)) {
                    testOutcomes.add(testOutcome);
                }
            }
        }
        return testOutcomes;
    }
}
