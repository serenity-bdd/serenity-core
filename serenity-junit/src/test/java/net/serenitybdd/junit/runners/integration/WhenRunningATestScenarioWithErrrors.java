package net.serenitybdd.junit.runners.integration;

import net.serenitybdd.junit.runners.AbstractTestStepRunnerTest;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.junit.rules.DisableThucydidesHistoryRule;
import net.thucydides.junit.rules.QuietThucydidesLoggingRule;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.samples.SampleNonWebScenarioWithError;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WhenRunningATestScenarioWithErrrors extends AbstractTestStepRunnerTest {

    @Rule
    public QuietThucydidesLoggingRule quietThucydidesLoggingRule = new QuietThucydidesLoggingRule();

    @Rule
    public DisableThucydidesHistoryRule disableThucydidesHistoryRule = new DisableThucydidesHistoryRule();

    @Test
    public void the_test_runner_records_the_steps_as_they_are_executed() throws InitializationError {

        SerenityRunner runner = new SerenityRunner(SampleNonWebScenarioWithError.class);
        runner.run(new RunNotifier());

        List<TestOutcome> executedScenarios = runner.getTestOutcomes();
        assertThat(executedScenarios.size(), is(3));
    }

}
