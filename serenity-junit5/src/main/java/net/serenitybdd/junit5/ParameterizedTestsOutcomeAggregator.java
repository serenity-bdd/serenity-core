package net.serenitybdd.junit5;

import net.thucydides.model.domain.*;
import net.thucydides.core.steps.BaseStepListener;
import net.thucydides.core.steps.StepEventBus;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.params.ParameterizedTest;

import java.util.*;
import java.util.stream.Collectors;

public class ParameterizedTestsOutcomeAggregator {

    private final List<TestOutcome> allTestOutcomes;

    public ParameterizedTestsOutcomeAggregator() {
        BaseStepListener baseStepListener = StepEventBus.getParallelEventBus().getBaseStepListener();
        allTestOutcomes = baseStepListener.getTestOutcomes();
    }

    public ParameterizedTestsOutcomeAggregator(BaseStepListener baseStepListener) {
        allTestOutcomes = baseStepListener.getTestOutcomes();
    }

    public ParameterizedTestsOutcomeAggregator(List<TestOutcome> testOutcomes) {
        allTestOutcomes = testOutcomes;
    }

    public List<TestOutcome> aggregateTestOutcomesByTestMethods() {
        List<TestOutcome> allOutcomes = getTestOutcomesForAllParameterSets();

        if (allOutcomes.isEmpty()) {
            return new ArrayList<>();
        } else {
            return aggregatedScenarioOutcomes(allOutcomes);
        }
    }

    private synchronized List<TestOutcome> aggregatedScenarioOutcomes(List<TestOutcome> allOutcomes) {

        Map<String, TestOutcome> scenarioOutcomes = new HashMap<>();

        for(int testOutcomeNumber = 0; testOutcomeNumber < allOutcomes.size(); testOutcomeNumber++) {
            TestOutcome testOutcome = allOutcomes.get(testOutcomeNumber);
            final String normalizedMethodName = baseMethodName(testOutcome);

            TestOutcome scenarioOutcome = scenarioOutcomeFor(normalizedMethodName, testOutcome, scenarioOutcomes);
            recordTestOutcomeAsSteps(testOutcome, scenarioOutcome);
            scenarioOutcome.setContext(testOutcome.getContext());

            if (testOutcome.isManual()) {
                scenarioOutcome = scenarioOutcome.setToManual();
            }

            if (testOutcome.isDataDriven()) {
                updateResultsForAnyExternalFailures(testOutcome, scenarioOutcomes.get(normalizedMethodName));
                if(scenarioOutcome.getDataTable() != null) {
                    List<DataTableRow> scenarioRows = scenarioOutcome.getDataTable().getRows();
                    List<DataTableRow> outcomeRows = testOutcome.getDataTable().getRows();
                    for (DataTableRow row : outcomeRows) {
                        if (!containRow(scenarioRows, row)) {
                            scenarioOutcome.addRow(row);
                        }
                    }
                } else {
                    scenarioOutcome.addDataFrom(testOutcome.getDataTable());
                }
                scenarioOutcome.updateDataTableResult(testOutcomeNumber, testOutcome.getResult());
            }
        }

        List<TestOutcome> aggregatedScenarioOutcomes = new ArrayList<>();
        aggregatedScenarioOutcomes.addAll(scenarioOutcomes.values());
        return aggregatedScenarioOutcomes;

    }

    private boolean containRow(List<DataTableRow> scenarioRows, DataTableRow expectedRow) {
        return scenarioRows.stream().anyMatch( row -> row.equalsIgnoringTheResult(expectedRow));
    }

    private void recordTestOutcomeAsSteps(TestOutcome testOutcome, TestOutcome scenarioOutcome) {
        final String name = alternativeMethodName(testOutcome);
        TestStep nestedStep = TestStep.forStepCalled(name).withResult(testOutcome.getResult());
        List<TestStep> testSteps = testOutcome.getTestSteps();

        if (testOutcome.getTestFailureCause() != null) {
            nestedStep.failedWith(testOutcome.getTestFailureCause().getOriginalCause());
        }

        if (!testSteps.isEmpty()) {
            for (TestStep nextStep : testSteps) {
                nextStep.setDescription(normalizeTestStepDescription(nextStep.getDescription(),
                        scenarioOutcome.getTestSteps().size() + 1));
                nestedStep.addChildStep(nextStep);
                nestedStep.setDuration(nextStep.getDuration() + nestedStep.getDuration());
            }
        }

        if (nestedStep.getDuration() == 0) {
            nestedStep.setDuration(testOutcome.getDuration());
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
            //TODO
            //scenarioOutcome.addFailingStepAsSibling(new AssertionError(testOutcome.getTestFailureMessage()));
        }
    }

    private boolean rowResultsAreInconsistantWithOverallResult(TestOutcome testOutcome) {
        TestResult overallRowResult = overallResultFrom(testOutcome.getDataTable().getRows());
        return (testOutcome.isError() || testOutcome.isFailure() || testOutcome.isCompromised())
                && (!testOutcome.getDataTable().getRows().isEmpty())
                && (testOutcome.getResult() != overallRowResult);
    }

    private TestResult overallResultFrom(List<DataTableRow> rows) {

        List<TestResult> resultsOfEachRow = rows.stream()
                .map(DataTableRow::getResult)
                .collect(Collectors.toList());

        return TestResultList.overallResultFrom(resultsOfEachRow);
    }

    private String normalizeTestStepDescription(String description, int index) {
        return StringUtils.replace(description, "[1]", "[" + index + "]");
    }

    private TestOutcome createScenarioOutcome(TestOutcome parameterizedOutcome) {
        TestOutcome testOutcome = TestOutcome.forTest(baseMethodName(parameterizedOutcome),
                parameterizedOutcome.getTestCase());

        return testOutcome;
    }

    private String baseMethodName(TestOutcome testOutcome) {
        return testOutcome.getName(); //Updated the baseMethodName so that we can effectively handle display names, display name generators, and methods without JUnit custom display names.
    }

    private String alternativeMethodName(TestOutcome testOutcome) {
        // Any parameterized test name attributes overrides the qualified name
        if (hasParameterizedTestName(testOutcome)) {
            return testOutcome.getTitle();
        }

        Optional<String> qualifier = testOutcome.getQualifier();
        if (qualifier != null && qualifier.isPresent()) {
            return testOutcome.getTitle(false) + " " + testOutcome.getQualifier().get();
        } else {
            return testOutcome.getTitle();
        }
    }

    private boolean hasParameterizedTestName(TestOutcome testOutcome) {
        if (testOutcome.getTestCase() == null) {
            return false;
        }
        String parameterizedTestName = Arrays.stream(testOutcome.getTestCase().getDeclaredMethods())
                .filter(method -> method.getName().equals(testOutcome.getMethodName()))
                .filter(method -> method.isAnnotationPresent(ParameterizedTest.class))
                .map(method -> method.getAnnotation(ParameterizedTest.class).name())
                .findFirst()
                .orElse("");

        return (!parameterizedTestName.isEmpty());
    }

    public List<TestOutcome> getTestOutcomesForAllParameterSets() {
        List<TestOutcome> testOutcomes = new ArrayList<>();
        for (TestOutcome testOutcome : allTestOutcomes) {
            //if (!testOutcomes.contains(testOutcome)) {
            testOutcomes.add(withParentStepsMerged(testOutcome));
            //testOutcomes.add(testOutcome);
            //}
        }
        return testOutcomes;
    }

    private static TestOutcome withParentStepsMerged(TestOutcome testOutcome) {
        if ( (testOutcome.getTestSteps().size() == 1) && testOutcome.getTestSteps().get(0).getDescription().startsWith("Example ") ){
            String testStepQualifier = testOutcome.getTestSteps().get(0).getDescription().replaceAll("Example \\d+:","");
            List<TestStep> childSteps = testOutcome.getTestSteps().get(0).getChildren();
            return testOutcome.withQualifier(testStepQualifier).withSteps(childSteps);
        } else {
            return testOutcome;
        }
    }
}
