package net.serenitybdd.junit5.datadriven;

import net.serenitybdd.junit5.AbstractTestStepRunnerTest;
import net.serenitybdd.junit5.datadriven.samples.*;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.model.ThucydidesSystemProperty;
import net.thucydides.model.domain.DataTable;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.domain.TestResult;
import net.thucydides.model.environment.MockEnvironmentVariables;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Test-Driven Development tests for JUnit 5 @TestFactory support.
 *
 * These tests define the expected behavior for dynamic tests in Serenity BDD.
 * The key requirement (from GitHub issue #3630) is that each dynamic test
 * should produce its own test outcome in the Serenity report, rather than
 * overwriting previous outcomes.
 *
 * Dynamic test unique IDs follow this pattern:
 * [engine:junit-jupiter]/[class:...]/[test-factory:methodName()]/[dynamic-test:#N]
 */
public class WhenRunningDynamicTestFactories extends AbstractTestStepRunnerTest {

    MockEnvironmentVariables environmentVariables;
    WebDriverFactory webDriverFactory;

    @BeforeEach
    public void createATestableDriverFactory() {
        MockitoAnnotations.initMocks(this);
        environmentVariables = new MockEnvironmentVariables();
        webDriverFactory = new WebDriverFactory(environmentVariables);
        StepEventBus.getParallelEventBus().clear();
        StepEventBus.setNoCleanupForStickyBuses(true);
    }

    @Nested
    @DisplayName("Basic Dynamic Test Support")
    class BasicDynamicTestSupport {

        @Test
        @DisplayName("Each dynamic test should have its own test outcome")
        public void each_dynamic_test_should_have_its_own_test_outcome() {
            // Given a test class with @TestFactory producing 3 dynamic tests
            runTestForClass(SimpleDynamicTestSample.class);

            // Then each dynamic test should have its own event bus and test outcome
            String baseId = "[engine:junit-jupiter]/[class:net.serenitybdd.junit5.datadriven.samples.SimpleDynamicTestSample]/[test-factory:simpleDynamicTests()]";

            for (int i = 1; i <= 3; i++) {
                String dynamicTestId = baseId + "/[dynamic-test:#" + i + "]";
                StepEventBus eventBus = StepEventBus.eventBusFor(dynamicTestId);
                List<TestOutcome> outcomes = eventBus.getBaseStepListener().getTestOutcomes();

                // Each dynamic test should have exactly 1 outcome
                assertThat("Dynamic test #" + i + " should have 1 outcome",
                        outcomes.size(), is(1));

                // Clean up
                StepEventBus.forceClearEventBusFor(dynamicTestId);
            }
        }

        @Test
        @DisplayName("Dynamic test display names should be preserved in test outcomes")
        public void dynamic_test_display_names_should_be_preserved() {
            // Given a test class with named dynamic tests
            runTestForClass(SimpleDynamicTestSample.class);

            String baseId = "[engine:junit-jupiter]/[class:net.serenitybdd.junit5.datadriven.samples.SimpleDynamicTestSample]/[test-factory:simpleDynamicTests()]";
            String[] expectedNames = {"Test First", "Test Second", "Test Third"};

            for (int i = 1; i <= 3; i++) {
                String dynamicTestId = baseId + "/[dynamic-test:#" + i + "]";
                StepEventBus eventBus = StepEventBus.eventBusFor(dynamicTestId);
                List<TestOutcome> outcomes = eventBus.getBaseStepListener().getTestOutcomes();

                assertThat("Dynamic test #" + i + " should have 1 outcome",
                        outcomes.size(), is(1));

                // The display name should be captured
                TestOutcome outcome = outcomes.get(0);
                assertThat("Dynamic test name should match",
                        outcome.getName(), containsString(expectedNames[i - 1]));

                StepEventBus.forceClearEventBusFor(dynamicTestId);
            }
        }

        @Test
        @DisplayName("All dynamic tests should pass when they contain passing assertions")
        public void all_dynamic_tests_should_pass() {
            runTestForClass(SimpleDynamicTestSample.class);

            String baseId = "[engine:junit-jupiter]/[class:net.serenitybdd.junit5.datadriven.samples.SimpleDynamicTestSample]/[test-factory:simpleDynamicTests()]";

            for (int i = 1; i <= 3; i++) {
                String dynamicTestId = baseId + "/[dynamic-test:#" + i + "]";
                StepEventBus eventBus = StepEventBus.eventBusFor(dynamicTestId);
                List<TestOutcome> outcomes = eventBus.getBaseStepListener().getTestOutcomes();

                assertThat(outcomes.size(), is(1));
                assertThat("Dynamic test #" + i + " should pass",
                        outcomes.get(0).getResult(), is(TestResult.SUCCESS));

                StepEventBus.forceClearEventBusFor(dynamicTestId);
            }
        }
    }

    @Nested
    @DisplayName("Dynamic Tests with Failures")
    class DynamicTestsWithFailures {

        @Test
        @DisplayName("Failed dynamic tests should be tracked individually")
        public void failed_dynamic_tests_should_be_tracked_individually() {
            runTestForClass(DynamicTestWithFailuresSample.class);

            String baseId = "[engine:junit-jupiter]/[class:net.serenitybdd.junit5.datadriven.samples.DynamicTestWithFailuresSample]/[test-factory:dynamicTestsWithMixedResults()]";

            // Test 1 should pass
            String test1Id = baseId + "/[dynamic-test:#1]";
            StepEventBus eventBus1 = StepEventBus.eventBusFor(test1Id);
            List<TestOutcome> outcomes1 = eventBus1.getBaseStepListener().getTestOutcomes();
            assertThat(outcomes1.size(), is(1));
            assertThat("First dynamic test should pass",
                    outcomes1.get(0).getResult(), is(TestResult.SUCCESS));

            // Test 2 should fail
            String test2Id = baseId + "/[dynamic-test:#2]";
            StepEventBus eventBus2 = StepEventBus.eventBusFor(test2Id);
            List<TestOutcome> outcomes2 = eventBus2.getBaseStepListener().getTestOutcomes();
            assertThat(outcomes2.size(), is(1));
            assertThat("Second dynamic test should fail",
                    outcomes2.get(0).getResult(), is(TestResult.FAILURE));

            // Test 3 should pass
            String test3Id = baseId + "/[dynamic-test:#3]";
            StepEventBus eventBus3 = StepEventBus.eventBusFor(test3Id);
            List<TestOutcome> outcomes3 = eventBus3.getBaseStepListener().getTestOutcomes();
            assertThat(outcomes3.size(), is(1));
            assertThat("Third dynamic test should pass",
                    outcomes3.get(0).getResult(), is(TestResult.SUCCESS));

            // Cleanup
            StepEventBus.forceClearEventBusFor(test1Id);
            StepEventBus.forceClearEventBusFor(test2Id);
            StepEventBus.forceClearEventBusFor(test3Id);
        }

        @Test
        @DisplayName("Failure in one dynamic test should not affect others")
        public void failure_in_one_dynamic_test_should_not_affect_others() {
            runTestForClass(DynamicTestWithFailuresSample.class);

            String baseId = "[engine:junit-jupiter]/[class:net.serenitybdd.junit5.datadriven.samples.DynamicTestWithFailuresSample]/[test-factory:dynamicTestsWithMixedResults()]";

            int passCount = 0;
            int failCount = 0;

            for (int i = 1; i <= 3; i++) {
                String dynamicTestId = baseId + "/[dynamic-test:#" + i + "]";
                StepEventBus eventBus = StepEventBus.eventBusFor(dynamicTestId);
                List<TestOutcome> outcomes = eventBus.getBaseStepListener().getTestOutcomes();

                if (!outcomes.isEmpty()) {
                    if (outcomes.get(0).getResult() == TestResult.SUCCESS) {
                        passCount++;
                    } else if (outcomes.get(0).getResult() == TestResult.FAILURE) {
                        failCount++;
                    }
                }
                StepEventBus.forceClearEventBusFor(dynamicTestId);
            }

            assertThat("Should have 2 passing tests", passCount, is(2));
            assertThat("Should have 1 failing test", failCount, is(1));
        }
    }

    @Nested
    @DisplayName("Multiple TestFactory Methods")
    class MultipleTestFactoryMethods {

        @Test
        @DisplayName("Each TestFactory method should produce separate outcomes")
        public void each_test_factory_should_produce_separate_outcomes() {
            runTestForClass(MultipleDynamicFactorySample.class);

            String classPath = "[engine:junit-jupiter]/[class:net.serenitybdd.junit5.datadriven.samples.MultipleDynamicFactorySample]";

            // First factory produces 2 tests
            String firstFactoryBase = classPath + "/[test-factory:firstFactory()]";
            for (int i = 1; i <= 2; i++) {
                String testId = firstFactoryBase + "/[dynamic-test:#" + i + "]";
                StepEventBus eventBus = StepEventBus.eventBusFor(testId);
                List<TestOutcome> outcomes = eventBus.getBaseStepListener().getTestOutcomes();
                assertThat("First factory test #" + i + " should have outcome",
                        outcomes.size(), is(1));
                StepEventBus.forceClearEventBusFor(testId);
            }

            // Second factory produces 3 tests
            String secondFactoryBase = classPath + "/[test-factory:secondFactory()]";
            for (int i = 1; i <= 3; i++) {
                String testId = secondFactoryBase + "/[dynamic-test:#" + i + "]";
                StepEventBus eventBus = StepEventBus.eventBusFor(testId);
                List<TestOutcome> outcomes = eventBus.getBaseStepListener().getTestOutcomes();
                assertThat("Second factory test #" + i + " should have outcome",
                        outcomes.size(), is(1));
                StepEventBus.forceClearEventBusFor(testId);
            }
        }

        @Test
        @DisplayName("Dynamic tests from different factories should not interfere")
        public void dynamic_tests_from_different_factories_should_not_interfere() {
            runTestForClass(MultipleDynamicFactorySample.class);

            String classPath = "[engine:junit-jupiter]/[class:net.serenitybdd.junit5.datadriven.samples.MultipleDynamicFactorySample]";

            int totalOutcomes = 0;

            // Count outcomes from first factory
            String firstFactoryBase = classPath + "/[test-factory:firstFactory()]";
            for (int i = 1; i <= 2; i++) {
                String testId = firstFactoryBase + "/[dynamic-test:#" + i + "]";
                StepEventBus eventBus = StepEventBus.eventBusFor(testId);
                totalOutcomes += eventBus.getBaseStepListener().getTestOutcomes().size();
                StepEventBus.forceClearEventBusFor(testId);
            }

            // Count outcomes from second factory
            String secondFactoryBase = classPath + "/[test-factory:secondFactory()]";
            for (int i = 1; i <= 3; i++) {
                String testId = secondFactoryBase + "/[dynamic-test:#" + i + "]";
                StepEventBus eventBus = StepEventBus.eventBusFor(testId);
                totalOutcomes += eventBus.getBaseStepListener().getTestOutcomes().size();
                StepEventBus.forceClearEventBusFor(testId);
            }

            // Should have 5 total outcomes (2 from first + 3 from second)
            assertThat("Should have 5 total test outcomes", totalOutcomes, is(5));
        }
    }

    @Nested
    @DisplayName("Dynamic Tests with Nested Containers")
    class DynamicTestsWithContainers {

        @Test
        @DisplayName("Tests inside DynamicContainers should each have their own outcome")
        public void tests_inside_containers_should_have_own_outcomes() {
            runTestForClass(DynamicContainerSample.class);

            String classPath = "[engine:junit-jupiter]/[class:net.serenitybdd.junit5.datadriven.samples.DynamicContainerSample]";
            String factoryPath = classPath + "/[test-factory:dynamicTestsWithContainers()]";

            // Container 1 (User) has 2 tests
            String container1 = factoryPath + "/[dynamic-container:#1]";
            for (int i = 1; i <= 2; i++) {
                String testId = container1 + "/[dynamic-test:#" + i + "]";
                StepEventBus eventBus = StepEventBus.eventBusFor(testId);
                List<TestOutcome> outcomes = eventBus.getBaseStepListener().getTestOutcomes();
                assertThat("Container 1, test #" + i + " should have outcome",
                        outcomes.size(), is(1));
                StepEventBus.forceClearEventBusFor(testId);
            }

            // Container 2 (Admin) has 2 tests
            String container2 = factoryPath + "/[dynamic-container:#2]";
            for (int i = 1; i <= 2; i++) {
                String testId = container2 + "/[dynamic-test:#" + i + "]";
                StepEventBus eventBus = StepEventBus.eventBusFor(testId);
                List<TestOutcome> outcomes = eventBus.getBaseStepListener().getTestOutcomes();
                assertThat("Container 2, test #" + i + " should have outcome",
                        outcomes.size(), is(1));
                StepEventBus.forceClearEventBusFor(testId);
            }
        }
    }

    @Nested
    @DisplayName("Report Generation for Dynamic Tests")
    class ReportGenerationForDynamicTests {

        @TempDir
        Path tempDir;

        @Test
        @DisplayName("Dynamic tests from a TestFactory should be aggregated into a single report")
        public void dynamic_tests_should_be_aggregated_into_single_report() throws IOException {
            File outputDirectory = tempDir.resolve("serenity").toFile();
            System.setProperty(ThucydidesSystemProperty.SERENITY_OUTPUT_DIRECTORY.getPropertyName(),
                    outputDirectory.getAbsolutePath());

            // SimpleDynamicTestSample has 1 @TestFactory with 3 dynamic tests
            runTestForClass(SimpleDynamicTestSample.class);

            File[] jsonReports = outputDirectory.listFiles(new JSONFileFilter());

            // Dynamic tests should be aggregated into a single report (like parameterized tests)
            assertThat("Should generate JSON reports", jsonReports, is(notNullValue()));
            assertThat("Should generate 1 aggregated JSON report for the TestFactory",
                    jsonReports.length, is(1));

            // The single report should contain all dynamic test names
            String reportContent = Files.readString(jsonReports[0].toPath());

            assertThat("Report should contain 'Test First'",
                    reportContent, containsString("Test First"));
            assertThat("Report should contain 'Test Second'",
                    reportContent, containsString("Test Second"));
            assertThat("Report should contain 'Test Third'",
                    reportContent, containsString("Test Third"));
        }

        @Test
        @DisplayName("Aggregated dynamic tests should have a data table like parameterized tests")
        public void aggregated_dynamic_tests_should_have_data_table() throws IOException {
            File outputDirectory = tempDir.resolve("serenity-datatable").toFile();
            System.setProperty(ThucydidesSystemProperty.SERENITY_OUTPUT_DIRECTORY.getPropertyName(),
                    outputDirectory.getAbsolutePath());

            // SimpleDynamicTestSample has 1 @TestFactory with 3 dynamic tests
            runTestForClass(SimpleDynamicTestSample.class);

            File[] jsonReports = outputDirectory.listFiles(new JSONFileFilter());

            assertThat("Should generate JSON reports", jsonReports, is(notNullValue()));
            assertThat("Should generate 1 aggregated JSON report", jsonReports.length, is(1));

            // The report should contain a dataTable structure
            String reportContent = Files.readString(jsonReports[0].toPath());

            // Verify the dataTable key exists
            assertThat("Report should contain dataTable",
                    reportContent, containsString("\"dataTable\""));

            // Verify the header "Test" exists
            assertThat("Report should have 'Test' header in data table",
                    reportContent, containsString("\"headers\""));

            // Verify all test names are in the data table values
            assertThat("Data table should contain 'Test First'",
                    reportContent, containsString("Test First"));
            assertThat("Data table should contain 'Test Second'",
                    reportContent, containsString("Test Second"));
            assertThat("Data table should contain 'Test Third'",
                    reportContent, containsString("Test Third"));
        }

        @Test
        @DisplayName("Data table rows should have correct test results")
        public void data_table_rows_should_have_correct_results() throws IOException {
            File outputDirectory = tempDir.resolve("serenity-results").toFile();
            System.setProperty(ThucydidesSystemProperty.SERENITY_OUTPUT_DIRECTORY.getPropertyName(),
                    outputDirectory.getAbsolutePath());

            // DynamicTestWithFailuresSample has mixed pass/fail results
            runTestForClass(DynamicTestWithFailuresSample.class);

            File[] jsonReports = outputDirectory.listFiles(new JSONFileFilter());

            assertThat("Should generate JSON reports", jsonReports, is(notNullValue()));
            assertThat("Should generate 1 aggregated JSON report", jsonReports.length, is(1));

            String reportContent = Files.readString(jsonReports[0].toPath());

            // Verify dataTable exists with results
            assertThat("Report should contain dataTable",
                    reportContent, containsString("\"dataTable\""));

            // Verify SUCCESS results (tests 1 and 3 pass)
            assertThat("Report should contain SUCCESS results",
                    reportContent, containsString("SUCCESS"));

            // Verify FAILURE result (test 2 fails)
            assertThat("Report should contain FAILURE result",
                    reportContent, containsString("FAILURE"));
        }
    }

    private static class JSONFileFilter implements FilenameFilter {
        @Override
        public boolean accept(File directory, String filename) {
            return filename.endsWith(".json") && !filename.startsWith("manifest");
        }
    }
}