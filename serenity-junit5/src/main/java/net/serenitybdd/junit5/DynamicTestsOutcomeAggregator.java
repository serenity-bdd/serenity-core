package net.serenitybdd.junit5;

import net.thucydides.model.domain.DataTable;
import net.thucydides.model.domain.DataTableRow;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.domain.TestStep;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Aggregates test outcomes from JUnit 5 @TestFactory dynamic tests into a single report
 * per factory method. Similar to how parameterized tests are aggregated.
 */
public class DynamicTestsOutcomeAggregator {

    private final List<TestOutcome> allTestOutcomes;

    // Pattern to extract the test-factory method identifier from dynamic test unique IDs
    // e.g., [engine:junit-jupiter]/[class:...]/[test-factory:methodName()]/[dynamic-test:#1]
    private static final Pattern TEST_FACTORY_PATTERN = Pattern.compile("\\[test-factory:([^\\]]+)\\]");

    public DynamicTestsOutcomeAggregator(List<TestOutcome> testOutcomes) {
        this.allTestOutcomes = testOutcomes;
    }

    /**
     * Aggregates dynamic test outcomes by their parent TestFactory method.
     * Each dynamic test becomes a nested step within the aggregated outcome.
     *
     * @return A list of aggregated test outcomes, one per TestFactory method
     */
    public List<TestOutcome> aggregateTestOutcomesByTestFactory() {
        if (allTestOutcomes.isEmpty()) {
            return new ArrayList<>();
        }

        // Group outcomes by their TestFactory method
        Map<String, List<TestOutcome>> outcomesByFactory = new LinkedHashMap<>();

        for (TestOutcome outcome : allTestOutcomes) {
            String factoryKey = extractTestFactoryKey(outcome);
            outcomesByFactory.computeIfAbsent(factoryKey, k -> new ArrayList<>()).add(outcome);
        }

        // Create aggregated outcomes for each factory
        List<TestOutcome> aggregatedOutcomes = new ArrayList<>();
        for (Map.Entry<String, List<TestOutcome>> entry : outcomesByFactory.entrySet()) {
            TestOutcome aggregated = createAggregatedOutcome(entry.getKey(), entry.getValue());
            if (aggregated != null) {
                aggregatedOutcomes.add(aggregated);
            }
        }

        return aggregatedOutcomes;
    }

    /**
     * Extracts the TestFactory method identifier from a test outcome's unique ID.
     */
    private String extractTestFactoryKey(TestOutcome outcome) {
        String id = outcome.getId();
        if (id == null) {
            // Fallback to method name
            return outcome.getMethodName() != null ? outcome.getMethodName() : "unknown";
        }

        Matcher matcher = TEST_FACTORY_PATTERN.matcher(id);
        if (matcher.find()) {
            // Extract everything up to and including the test-factory part
            int endIndex = matcher.end();
            return id.substring(0, endIndex);
        }

        // Fallback to method name
        return outcome.getMethodName() != null ? outcome.getMethodName() : "unknown";
    }

    /**
     * Creates an aggregated test outcome from a list of dynamic test outcomes.
     */
    private TestOutcome createAggregatedOutcome(String factoryKey, List<TestOutcome> dynamicOutcomes) {
        if (dynamicOutcomes.isEmpty()) {
            return null;
        }

        // Use the first outcome as a template for the aggregated outcome
        TestOutcome firstOutcome = dynamicOutcomes.get(0);

        // Extract the factory method name for the test name
        String factoryMethodName = extractFactoryMethodName(factoryKey);

        // Create a new aggregated test outcome
        TestOutcome aggregatedOutcome = TestOutcome.forTest(
                factoryMethodName,
                firstOutcome.getTestCase()
        );

        // Copy common attributes from the first outcome
        if (firstOutcome.getUserStory() != null) {
            aggregatedOutcome.setUserStory(firstOutcome.getUserStory());
        }

        // Record each dynamic test as a step within the aggregated outcome
        for (TestOutcome dynamicOutcome : dynamicOutcomes) {
            recordDynamicTestAsStep(dynamicOutcome, aggregatedOutcome);
        }

        // Create and add a data table for the dynamic tests (like parameterized tests)
        DataTable dataTable = createDataTableFromDynamicTests(dynamicOutcomes);
        aggregatedOutcome.addDataFrom(dataTable);

        // Update the result for each row in the data table
        for (int i = 0; i < dynamicOutcomes.size(); i++) {
            aggregatedOutcome.updateDataTableResult(i, dynamicOutcomes.get(i).getResult());
        }

        // Add tags from all dynamic tests
        for (TestOutcome dynamicOutcome : dynamicOutcomes) {
            dynamicOutcome.getTags().forEach(aggregatedOutcome::addTag);
        }

        aggregatedOutcome.setContext(firstOutcome.getContext());
        aggregatedOutcome.setTestSource(firstOutcome.getTestSource());

        return aggregatedOutcome;
    }

    /**
     * Creates a DataTable from the dynamic test outcomes, similar to parameterized tests.
     */
    private DataTable createDataTableFromDynamicTests(List<TestOutcome> dynamicOutcomes) {
        List<String> headers = Collections.singletonList("Test");
        List<DataTableRow> rows = new ArrayList<>();

        for (int i = 0; i < dynamicOutcomes.size(); i++) {
            TestOutcome outcome = dynamicOutcomes.get(i);
            String testName = outcome.getName();
            List<Object> values = Collections.singletonList(testName);
            DataTableRow row = new DataTableRow(values, i);
            row.setResult(outcome.getResult());
            rows.add(row);
        }

        return DataTable.withHeaders(headers).andRowData(rows).build();
    }

    /**
     * Extracts the factory method name from the factory key.
     */
    private String extractFactoryMethodName(String factoryKey) {
        Matcher matcher = TEST_FACTORY_PATTERN.matcher(factoryKey);
        if (matcher.find()) {
            String methodWithParens = matcher.group(1);
            // Remove trailing () if present
            if (methodWithParens.endsWith("()")) {
                return methodWithParens.substring(0, methodWithParens.length() - 2);
            }
            return methodWithParens;
        }
        return "dynamicTests";
    }

    /**
     * Records a dynamic test outcome as a nested step in the aggregated outcome.
     */
    private void recordDynamicTestAsStep(TestOutcome dynamicOutcome, TestOutcome aggregatedOutcome) {
        String stepName = dynamicOutcome.getName();

        TestStep nestedStep = TestStep.forStepCalled(stepName).withResult(dynamicOutcome.getResult());

        // Include any failure information
        if (dynamicOutcome.getTestFailureCause() != null) {
            nestedStep.failedWith(dynamicOutcome.getTestFailureCause().getOriginalCause());
        }

        // Copy any child steps from the dynamic test
        List<TestStep> childSteps = dynamicOutcome.getTestSteps();
        if (!childSteps.isEmpty()) {
            for (TestStep childStep : childSteps) {
                nestedStep.addChildStep(childStep);
            }
        }

        // Set duration
        nestedStep.setDuration(dynamicOutcome.getDuration());

        // Record the step
        aggregatedOutcome.recordStep(nestedStep);
    }
}