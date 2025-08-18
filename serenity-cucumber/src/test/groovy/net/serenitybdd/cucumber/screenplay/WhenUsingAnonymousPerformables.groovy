package net.serenitybdd.cucumber.screenplay;

import net.serenitybdd.cucumber.integration.SimpleScreenplayAnonymousPerformableFailingAndPassingScenario;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.domain.TestResult
import net.thucydides.model.domain.TestStep;
import net.thucydides.model.reports.OutcomeFormat;
import net.thucydides.model.reports.TestOutcomeLoader;
import org.assertj.core.util.Files
import spock.lang.Specification;

import static io.cucumber.junit.CucumberRunner.serenityRunnerForCucumberTestRunner;

class WhenUsingAnonymousPerformables extends Specification  {

    File outputDirectory
    List<TestOutcome> recordedTestOutcomes

    def setup() {
        outputDirectory = Files.newTemporaryFolder()

        def runtime = serenityRunnerForCucumberTestRunner(SimpleScreenplayAnonymousPerformableFailingAndPassingScenario.class, outputDirectory)
        runtime.run()
        recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory).sort { it.name }
    }


    def "should run steps after an anonymous performable passed"() {
        given:
        TestOutcome testOutcome = getTestOutcomes("Should run steps after an anonymous performable passed")
        ArrayList<TestResult> stepResults = testOutcome.testSteps.collect(({ step -> step.result }))
        expect:
        stepResults == [TestResult.SUCCESS, TestResult.SUCCESS, TestResult.SUCCESS]
    }

    def "should not run steps after an anonymous performable failed"() {
        given:
        TestOutcome testOutcome = getTestOutcomes("Should not run steps after an anonymous performable failed")
        ArrayList<TestResult> stepResults = testOutcome.testSteps.collect(({ step -> step.result }))
        expect:
        stepResults == [TestResult.SUCCESS, TestResult.ERROR, TestResult.SKIPPED]
    }

    //
    def "should run example after an anonymous performable failed"() {
        given:
        TestOutcome testOutcome = getTestOutcomes("Should run example after an anonymous performable failed")
        ArrayList<TestResult> stepResults = testOutcome.testSteps.collect(({ step -> step.result }))
        ArrayList<TestStep> children = testOutcome.testSteps.collect(({ step -> step.children }))
        expect:
        stepResults == [TestResult.ERROR, TestResult.SUCCESS]
        children[0].result[2] == TestResult.SKIPPED
        children[0].size() == 3
        children[1].size() == 3
    }

    TestOutcome getTestOutcomes(String methodName) {
        def matchingOutcome = recordedTestOutcomes.find { it.name.equals(methodName) }
        if (!matchingOutcome) {
            throw new AssertionError("No matching test method called $methodName")
        }
        return matchingOutcome
    }
}
