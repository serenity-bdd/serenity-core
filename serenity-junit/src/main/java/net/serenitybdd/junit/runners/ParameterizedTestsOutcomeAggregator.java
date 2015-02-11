package net.serenitybdd.junit.runners;

import com.google.common.collect.Lists;
import net.thucydides.core.model.DataTableRow;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestResultList;
import net.thucydides.core.model.TestStep;
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
            return Lists.<TestOutcome>newArrayList();
        } else {
            return aggregatedScenarioOutcomes(allOutcomes);
        }

    }

    private List<TestOutcome> aggregatedScenarioOutcomes(List<TestOutcome> allOutcomes) {
        Map<String, TestOutcome> scenarioOutcomes = new HashMap<>();

        for (TestOutcome testOutcome : allOutcomes) {
            String normalizedMethodName = normalizeMethodName(testOutcome);

            if (!scenarioOutcomes.containsKey(normalizedMethodName)) {
                TestOutcome scenarioOutcome = createScenarioOutcome(testOutcome);
                scenarioOutcomes.put(normalizedMethodName, scenarioOutcome);
            }
            List<TestStep> testSteps = testOutcome.getTestSteps();
            if (testSteps.isEmpty()) {
                TestStep nestedStep = TestStep.forStepCalled(testOutcome.getTitle()).withResult(testOutcome.getResult());
                scenarioOutcomes.get(normalizedMethodName).recordStep(nestedStep);
            } else {
                TestStep nestedStep = TestStep.forStepCalled(testOutcome.getTitle()).withResult(testOutcome.getResult());
                for (TestStep nextStep : testSteps) {
                    nextStep.setDescription(normalizeTestStepDescription(nextStep.getDescription(),
                            scenarioOutcomes.get(normalizedMethodName).getTestSteps().size() + 1));
                    nestedStep.addChildStep(nextStep);
                }
                scenarioOutcomes.get(normalizedMethodName).recordStep(nestedStep);
            }
            if (testOutcome.isDataDriven()) {
                updateResultsForAnyExternalFailures(scenarioOutcomes.get(normalizedMethodName), testOutcome);
                scenarioOutcomes.get(normalizedMethodName).addDataFrom(testOutcome.getDataTable());
            }

        }

        List<TestOutcome> aggregatedScenarioOutcomes = new ArrayList<>();
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
        return TestOutcome.forTest(normalizeMethodName(parameterizedOutcome), parameterizedOutcome.getTestCase());
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
