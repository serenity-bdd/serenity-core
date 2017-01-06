package net.thucydides.junit.runners

import net.serenitybdd.junit.runners.SerenityParameterizedRunner
import net.serenitybdd.junit.runners.SerenityRunner
import net.thucydides.core.model.TestResult
import net.thucydides.core.util.MockEnvironmentVariables
import net.thucydides.core.webdriver.SerenityWebdriverManager
import net.thucydides.core.configuration.SystemPropertiesConfiguration
import net.thucydides.core.webdriver.WebDriverFactory
import net.thucydides.samples.*
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.junit.runner.notification.RunNotifier
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll

import java.nio.file.Path
import java.nio.file.Paths

import static net.thucydides.core.model.TestResult.*
import static net.thucydides.junit.runners.TestOutcomeChecks.resultsFrom

class WhenRunningTestScenarios extends Specification {

    def environmentVariables = new MockEnvironmentVariables()
    def configuration = new SystemPropertiesConfiguration(environmentVariables)
    def webDriverFactory = new WebDriverFactory(environmentVariables)
    File temporaryDirectory

    @Rule
    TemporaryFolder temporaryFolder

    def setup() {
        temporaryDirectory = temporaryFolder.newFolder()
    }



    def "should be able to specify a different driver"() {
        given:
        def runner = new ThucydidesRunner(SamplePassingScenarioUsingHtmlUnit, webDriverFactory)
        when:
        runner.run(new RunNotifier())
        then:
        runner.testOutcomes.size() == 3
    }

    def "should be able to use page objects directly in a test"() {
        given:
            def runner = new SerenityRunner(SamplePassingScenarioWithPageObjects)
        when:
            runner.run(new RunNotifier())
        then:
            runner.testOutcomes.get(0).result == TestResult.SUCCESS
    }

    @Ignore
    def "should be able to record the driver used for a test"() {
        given:
            def runner = new SerenityRunner(SamplePassingScenarioUsingFirefox);
        when:
            runner.run(new RunNotifier())
            def drivers = runner.testOutcomes.collect {it.driver}
        then:
            drivers.contains("firefox")
    }

    @Ignore
    def "should be able to record the driver used for a test when a different driver is specified"() {
        given:
            def runner = new SerenityRunner(SamplePassingScenarioUsingHtmlUnit);
        when:
            runner.run(new RunNotifier())
            def drivers = runner.testOutcomes.collect {it.driver}
        then:
            drivers.contains("htmlunit")
    }

    def "should not record a driver used for a non-web test"() {
        given:
            def runner = new SerenityRunner(SamplePassingNonWebScenario, webDriverFactory)
        when:
            runner.run(new RunNotifier())
            List<String> drivers = runner.testOutcomes.collect {it.driver}
        then:
            drivers.each { driver -> assert driver == null }
    }

    @Ignore
    def "should be able to record a different driver for an individual test"() {
        given:
            def runner = new SerenityRunner(SamplePassingScenarioUsingDifferentBrowsersForEachTest, webDriverFactory)
        when:
            runner.run(new RunNotifier())
            def drivers = runner.testOutcomes.collect {it.driver}
        then:
            drivers.contains("firefox") && drivers.contains("htmlunit")
    }


    def "should record the steps that are executed"() {
        given:
        def runner = new ThucydidesRunner(SamplePassingScenario, webDriverFactory)
        when:
        runner.run(new RunNotifier())
        def outcomes = runner.testOutcomes
        def results = resultsFrom(outcomes)
        then:
        outcomes.size() == 3
        and:
        results["happy_day_scenario"].title == "Happy day scenario"
        results["happy_day_scenario"].testSteps.size() == 4

        results["edge_case_1"].title == "Edge case 1"
        results["edge_case_1"].testSteps.size() == 3

        results["edge_case_2"].title == "Edge case 2"
        results["edge_case_2"].testSteps.size() == 2
    }

    def "should record state between steps"() {
        given:
        def runner = new ThucydidesRunner(SampleScenarioWithStateVariables, webDriverFactory)
        when:
        runner.run(new RunNotifier())
        def outcomes = runner.testOutcomes;
        def results = resultsFrom(outcomes)
        then:
        outcomes.size() == 3
        results["joes_test"].result == SUCCESS
        results["jills_test"].result == SUCCESS
        results["no_ones_test"].result == SUCCESS

    }

    def "should mark @manual tests as manual"() {
        given:
            def runner = new ThucydidesRunner(SampleManualScenario, webDriverFactory)
        when:
            runner.run(new RunNotifier())
            def outcomes = runner.testOutcomes;
        then:
            outcomes[0].isManual()
    }

    def "should mark @manual data-driven tests as manual"() {
        given:
        def runner = new SerenityParameterizedRunner(SampleDataDrivenScenario)
        when:
        runner.run(new RunNotifier())
        def outcomes = runner.runners
        then:
        outcomes
    }

    def "an error in a nested non-step method should cause the test to fail"() {
        given:
        def runner = new ThucydidesRunner(SampleScenarioWithFailingNestedNonStepMethod, webDriverFactory)
        when:
        runner.run(new RunNotifier())
        def outcomes = runner.testOutcomes;
        def results = resultsFrom(outcomes)
        then:
        outcomes.size() == 3
        and:
        results["happy_day_scenario"].result == ERROR
        results["happy_day_scenario"].testSteps[2].result == ERROR
    }

    def "a failure in a nested step method should cause the test to fail"() {
        given:
        def runner = new ThucydidesRunner(SampleScenarioWithFailingNestedStepMethod, webDriverFactory)
        when:
        runner.run(new RunNotifier())
        def outcomes = runner.testOutcomes;
        def results = resultsFrom(outcomes)
        then:
        outcomes.size() == 3
        and:
        results["happy_day_scenario"].result == FAILURE
        results["happy_day_scenario"].testSteps[2].result == FAILURE
        results["happy_day_scenario"].testSteps[2].children[0].result == SUCCESS
        results["happy_day_scenario"].testSteps[2].errorMessage.contains "Oh crap!"
    }


    def "an error in a non-step method should be displayed as a failing step"() {
        given:
        def runner = new ThucydidesRunner(SampleScenarioWithFailingNonStepMethod, webDriverFactory)
        when:
        runner.run(new RunNotifier())
        def outcomes = runner.testOutcomes;
        def results = resultsFrom(outcomes)
        then:
        outcomes.size() == 3
        and:
        results["happy_day_scenario"].result == ERROR
        results["happy_day_scenario"].testSteps.size() == 3
    }

    def "pending tests should be reported as pending"() {
        given:
        def runner = new ThucydidesRunner(SamplePendingScenario, webDriverFactory)
        when:
        runner.run(new RunNotifier())
        def outcomes = runner.testOutcomes;
        then:
        outcomes[0].result == PENDING
        outcomes[0].testSteps == []
    }

    def "private annotated fields should be allowed"() {
        given:
        def runner = new ThucydidesRunner(SamplePassingScenarioWithPrivateFields, webDriverFactory)
        when:
        runner.run(new RunNotifier())
        def outcomes = runner.testOutcomes;
        def results = resultsFrom(outcomes)
        then:
        outcomes.size() == 3
        and:
        results["happy_day_scenario"].title == "Happy day scenario"
        results["happy_day_scenario"].testSteps.size() == 4

        results["edge_case_1"].title == "Edge case 1"
        results["edge_case_1"].testSteps.size() == 3

        results["edge_case_2"].title == "Edge case 2"
        results["edge_case_2"].testSteps.size() == 2
    }

    def "annotated fields should be allowed in parent classes"() {
        given:
        def runner = new ThucydidesRunner(SamplePassingScenarioWithFieldsInParent, webDriverFactory)
        when:
        runner.run(new RunNotifier())
        def outcomes = runner.testOutcomes;
        def results = resultsFrom(outcomes)
        then:
        outcomes.size() == 3
        and:
        results["happy_day_scenario"].title == "Happy day scenario"
        results["happy_day_scenario"].testSteps.size() == 4

        results["edge_case_1"].title == "Edge case 1"
        results["edge_case_1"].testSteps.size() == 3

        results["edge_case_2"].title == "Edge case 2"
        results["edge_case_2"].testSteps.size() == 2
    }

    @Unroll
    def "annotated tests should have expected results"() {
        given:
        def runner = new ThucydidesRunner(testclass, webDriverFactory)
        when:
        runner.run(new RunNotifier())
        def outcomes = runner.testOutcomes;
        def results = resultsFrom(outcomes)
        then:
        results["happy_day_scenario"].result == happy_day_result
        results["edge_case_1"].result == edge_case_1_result
        results["edge_case_2"].result == edge_case_2_result

        where:
        testclass                               | happy_day_result   | edge_case_1_result | edge_case_2_result
        SamplePassingScenarioWithPendingTests   | SUCCESS            | PENDING | PENDING
        SamplePassingScenarioWithIgnoredTests   | SUCCESS            | IGNORED | IGNORED
        SamplePassingScenarioWithEmptyTests     | SUCCESS            | SUCCESS | SUCCESS
        MockOpenStaticDemoPageWithFailureSample | FAILURE            | SUCCESS | SUCCESS
        MockOpenPageWithWebdriverErrorSample    | ERROR              | SUCCESS | SUCCESS
    }

    def "failing tests with no steps should still record the error"() {
        given:
        def runner = new ThucydidesRunner(SampleEmptyTestFailing, webDriverFactory)
        when:
        runner.run(new RunNotifier())
        def outcomes = runner.testOutcomes;
        then:
        outcomes.size() == 1
        outcomes[0].result == FAILURE
        outcomes[0].testFailureMessage == "TestException without any steps."
    }

    def "failing tests with with failure outside a step should still record the error"() {
        given:
            def runner = new ThucydidesRunner(SampleOutsideStepFailure, webDriverFactory)
        when:
            runner.run(new RunNotifier())
            def outcomes = runner.testOutcomes;
        then:
            outcomes.size() == 1
            outcomes[0].result == FAILURE
    }


    def "should skip test steps after a failure"() {
        given:
        def runner = new ThucydidesRunner(SingleTestScenario, webDriverFactory)
        when:
        runner.run(new RunNotifier())
        then:
        def steps = runner.testOutcomes[0].testSteps;
        def stepResults = steps.collect {it.result}
        stepResults == [SUCCESS, SUCCESS, IGNORED, SUCCESS, FAILURE, SKIPPED]
    }

    def "should skip any ignored tests"() {
        given:
        def runner = new ThucydidesRunner(TestIgnoredScenario, webDriverFactory)
        when:
        runner.run(new RunNotifier())
        then:
        runner.testOutcomes[0].result == IGNORED;
        runner.testOutcomes[0].testSteps == [];

    }

    def "should skip test steps after an error"() {
        given:
        def runner = new ThucydidesRunner(SampleNoSuchElementExceptionScenario, webDriverFactory)
        when:
        runner.run(new RunNotifier())
        def results = resultsFrom(runner.testOutcomes)
        then:
        def steps = results["failing_happy_day_scenario"].getTestSteps()
        def stepResults = steps.collect {it.result}
        stepResults == [SUCCESS, IGNORED, SUCCESS, ERROR, SKIPPED]
    }

    def "should record error message in the failing test step"() {
        given:
        def runner = new ThucydidesRunner(SingleTestScenario, webDriverFactory)
        when:
        runner.run(new RunNotifier())
        def steps = runner.testOutcomes[0].getTestSteps()
        println steps
        def failingStep = steps[4]
        then:
        failingStep.errorMessage.contains "Expected: is <2>"
        and:
        failingStep.exception.errorType == "java.lang.AssertionError"
    }

    def "when a test throws a runtime exception is should be recorded in the step"() {
        given:
        def runner = new ThucydidesRunner(SingleTestScenarioWithRuntimeException, webDriverFactory)
        when:
        runner.run(new RunNotifier())
        def steps = runner.testOutcomes[0].getTestSteps()
        def failingStep = steps[3]
        then:
        failingStep.exception.errorType == "java.lang.IllegalArgumentException"
    }

    def "should record the name of the test scenario"() {
        given:
        def runner = new ThucydidesRunner(SuccessfulSingleTestScenario, webDriverFactory)
        when:
        runner.run(new RunNotifier())
        then:
        runner.testOutcomes[0].title == "Happy day scenario"
    }

    def "should execute tests in groups"() {
        given:
        def runner = new ThucydidesRunner(TestScenarioWithGroups, webDriverFactory)
        when:
        runner.run(new RunNotifier())
        then:
        runner.testOutcomes.size() == 1
        and:
        runner.testOutcomes[0].testSteps.size() == 3
    }

    def "should record an acceptance test result for each test"() {
        given:
        def runner = new ThucydidesRunner(SamplePassingScenario, webDriverFactory)
        when:
        runner.run(new RunNotifier())
        then:
        runner.testOutcomes.size() == 3
    }

    def "should derive the user story from the test case class"() {
        given:
        def runner = new ThucydidesRunner(SuccessfulSingleTestScenario, webDriverFactory)
        when:
        runner.run(new RunNotifier())
        def outcome = runner.testOutcomes[0]
        then:
        outcome.userStory.name == "Successful single test scenario"
    }

    def "should record each step with a human-readable name"() {
        given:
        def runner = new ThucydidesRunner(SuccessfulSingleTestScenario, webDriverFactory)
        when:
        runner.run(new RunNotifier())
        def outcome = runner.testOutcomes[0]
        def firstStep = outcome.testSteps[0]
        then:
        firstStep.description == "Step that succeeds"
    }

    def "should be able to override default step names using the @Step annotation"() {
        given:
        def runner = new ThucydidesRunner(SuccessfulSingleTestScenario, webDriverFactory)
        when:
        runner.run(new RunNotifier())
        def outcome = runner.testOutcomes[0]
        def pendingStep = outcome.testSteps[2]
        then:
        pendingStep.description == "A pending step"
    }

    def "steps with a parameter should contain the parameter value in the description"() {
        given:
        def runner = new ThucydidesRunner(TestScenarioWithParameterizedSteps, webDriverFactory)
        when:
        runner.run(new RunNotifier())
        def outcome = runner.testOutcomes[0]
        def firstStep = outcome.testSteps[0]
        then:
        firstStep.description == "Step with a parameter: proportionOf"
    }

    def "steps with multiple parameters should contain the parameter values in the description"() {
        given:
        def runner = new ThucydidesRunner(TestScenarioWithParameterizedSteps, webDriverFactory)
        when:
        runner.run(new RunNotifier())
        def outcome = runner.testOutcomes[0]
        def firstStep = outcome.testSteps[1]
        then:
        firstStep.description == "Step with two parameters: proportionOf, 2"
    }

    def "should be able to override scenario titles using the @Title annotation"() {
        given:
        def runner = new ThucydidesRunner(AnnotatedSingleTestScenario, webDriverFactory)
        when:
        runner.run(new RunNotifier())
        def outcome = runner.testOutcomes[0]
        then:
        outcome.title == "Oh happy days!"
    }

    def "should not require a steps in a test"() {
        given:
        def runner = new ThucydidesRunner(SampleScenarioWithoutSteps)
        when:
        runner.run(new RunNotifier())
        then:
        runner.testOutcomes.size() == 3
    }

    def "should not require a webdriver in a test"() {
        given:
        def runner = new ThucydidesRunner(SimpleNonWebScenario)
        when:
        runner.run(new RunNotifier())
        then:
        runner.testOutcomes.size() == 2
    }

    def "should ignore close if the webdriver is not defined"() {
        when:
        def manager = new SerenityWebdriverManager(webDriverFactory, new SystemPropertiesConfiguration(environmentVariables));
        then:
        manager.closeDriver()
    }

    class ATestableThucydidesRunnerSample extends ThucydidesRunner {
        ATestableThucydidesRunnerSample(Class<?> klass, WebDriverFactory webDriverFactory) {
            super(klass, webDriverFactory)
        }

        @Override
        File getOutputDirectory() {
            return temporaryDirectory
        }
    }


    def "JSON test results should be written to the output directory"() {
        given:
        def runner = new ATestableThucydidesRunnerSample(SamplePassingScenario, webDriverFactory)
        when:
        runner.run(new RunNotifier())
        def jsonReports = reload(temporaryDirectory).list().findAll {it.endsWith(".json")}
        then:
        jsonReports.size() == 3
    }

    def "tests for multiple stories should be written to the output directory"() {
        when:
            new ATestableThucydidesRunnerSample(SamplePassingScenarioUsingHtmlUnit, webDriverFactory).run(new RunNotifier())
            new ATestableThucydidesRunnerSample(SampleFailingScenarioUsingHtmlUnit, webDriverFactory).run(new RunNotifier())
            def jsonReports = reload(temporaryDirectory).list().findAll {it.toLowerCase().endsWith(".json") && !it.startsWith("SERENITY-")}
        then:
        jsonReports.size() == 6
    }

    def "HTML test results should be written to the output directory"() {
        when:
            new ATestableThucydidesRunnerSample(SamplePassingScenarioUsingHtmlUnit, webDriverFactory).run(new RunNotifier())
            def htmlReports = reload(temporaryDirectory).list().findAll {it.toLowerCase().endsWith(".html")}
        then:
            htmlReports.size() == 3
    }


    @Unroll
    def "should be able to only run tests with a given tag at the class level"() {
        given:
            environmentVariables.setProperty("tags",tag)
            def webDriverFactory = new WebDriverFactory(environmentVariables)
            def runner = new ThucydidesRunner(SamplePassingScenario, webDriverFactory, configuration)
        when:
            runner.run(new RunNotifier())
        then:
            runner.testOutcomes.size() == testcount
        where:
            tag                     | testcount
            "module:M1"             | 3
            "module:M2"             | 0
            "module:M1, module:M2"  | 3
    }

    @Unroll
    def "should be able to only run tests with a given tag at the test level"() {
        given:
            environmentVariables.setProperty("tags",tag)
            def webDriverFactory = new WebDriverFactory(environmentVariables)
            def runner = new ThucydidesRunner(SamplePassingScenario, webDriverFactory, configuration)
        when:
            runner.run(new RunNotifier())
        then:
            runner.testOutcomes.size() == testcount
        where:
            tag                           | testcount
            "iteration:I1"                | 2
            "iteration:I2"                | 1
            "iteration:I1, iteration:I2"  | 3
            "iteration:I3"                | 0
    }

    private File reload(File old) {
        return Paths.get(old.getAbsolutePath()).toFile();
    }
    private Path reload(Path old) {
        return Paths.get(old.toAbsolutePath().toString());
    }
}
