package net.serenitybdd.junit5.samples.integration;

import net.serenitybdd.junit5.AbstractTestStepRunnerTest;
import net.serenitybdd.junit5.TestLauncher;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.samples.SamplePassingNonWebScenario;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;




public class WhenRunningANestedTest {

    @Test
    public void the_test_runner_records_the_name_of_the_test_scenario()  {

        TestLauncher.runTestForClass(JUnit5NestedExampleTest.class);

        List<TestOutcome> executedScenarios = StepEventBus.getEventBus().getBaseStepListener().getTestOutcomes();
        assertThat(executedScenarios.size(), equalTo(3));
        assertThat(inTheTestOutcomes(executedScenarios).theOutcomeFor("sampleTestForMethodA").getTitle(), is("Example test for method A"));
        assertThat(inTheTestOutcomes(executedScenarios).theOutcomeFor("sampleTestForMethodB").getTitle(), is("Sample test for method b"));
        assertThat(inTheTestOutcomes(executedScenarios).theOutcomeFor("sampleTestForMethodAOuterClass").getTitle(), is("Example test for method A outerClass"));

    }

    public AbstractTestStepRunnerTest.TestOutcomeChecker inTheTestOutcomes(List<TestOutcome> testOutcomes) {
        return new AbstractTestStepRunnerTest.TestOutcomeChecker(testOutcomes);
    }

}
