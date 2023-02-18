package net.serenitybdd.junit5.samples.integration;

import net.serenitybdd.junit5.AbstractTestStepRunnerTest;
import net.serenitybdd.junit5.TestLauncher;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.steps.StepEventBus;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;

public class WhenRunningATestWithGeneratedNames {

    @Test
    public void the_test_runner_records_the_name_of_the_test_scenario()  {

        TestLauncher.runTestForClass(JUnit5GeneratedExample.class);

        List<TestOutcome> executedScenarios = StepEventBus.getParallelEventBus().getBaseStepListener().getTestOutcomes();
        assertThat(executedScenarios.size(), equalTo(3));

        List<String> executedTestNames = executedScenarios.stream().map(TestOutcome::getName).collect(Collectors.toList());
        assertThat(executedTestNames, contains("Example test for method A outerClass","Example test for method A","sampleTestForMethodB"));
    }

    public AbstractTestStepRunnerTest.TestOutcomeChecker inTheTestOutcomes(List<TestOutcome> testOutcomes) {
        return new AbstractTestStepRunnerTest.TestOutcomeChecker(testOutcomes);
    }

}
