package net.serenitybdd.cucumber.outcomes.parallel

import io.cucumber.junit.CucumberJUnit5ParallelRunner
import net.serenitybdd.core.di.SerenityInfrastructure
import net.thucydides.model.domain.TestOutcome
import net.thucydides.model.domain.TestResult
import net.thucydides.model.domain.TestStep
import net.thucydides.model.reports.OutcomeFormat
import net.thucydides.model.reports.TestOutcomeLoader
import net.thucydides.model.util.TestResources
import net.thucydides.model.webdriver.Configuration
import org.assertj.core.util.Files
import spock.lang.Specification

class WhenCreatingSerenityTestOutcomesInParallel extends Specification {

    static File outputDirectory

    def setup() {
        outputDirectory = Files.newTemporaryFolder()
    }

    def cleanup() {
        outputDirectory.deleteDir()
    }

    def "should record failures for a failing scenario"() {
        given:
            Configuration configuration = SerenityInfrastructure.configuration
            configuration.setOutputDirectory(outputDirectory)
        when:
            CucumberJUnit5ParallelRunner.runFileFromClasspathInParallel("samples/failing_scenario.feature","net.serenitybdd.cucumber.integration.steps")
            List<TestOutcome> recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory).sort { it.name }
            TestOutcome testOutcome = recordedTestOutcomes[0]
            List<TestStep> stepResults = testOutcome.testSteps.collect { step -> step.result }
        then:
            testOutcome.result == TestResult.FAILURE
        and:
            stepResults.contains(TestResult.FAILURE)
        //  TODO: Fix known issue where the steps after a failed step appear as nested underneath the failed step.
        //  NOTE: Hic Sunt Dracones
        //  stepResults == [TestResult.SUCCESS, TestResult.SUCCESS, TestResult.SUCCESS, TestResult.FAILURE, TestResult.SKIPPED]
    }


    def "should generate a well-structured Serenity test outcome for each executed Cucumber scenario"() {
        given:
        Configuration configuration = SerenityInfrastructure.configuration
        configuration.setOutputDirectory(outputDirectory)

        when:
        CucumberJUnit5ParallelRunner.runFileFromClasspathInParallel("samples/simple_scenario.feature","net.serenitybdd.cucumber.integration.steps")

        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory).sort { it.name }
        def testOutcome = recordedTestOutcomes[0]
        def steps = testOutcome.testSteps.collect { step -> step.description }

        then:
        testOutcome.title == "A simple scenario"
    }

    def "should record results for each step"() {
        given:
              Configuration configuration = SerenityInfrastructure.configuration
        configuration.setOutputDirectory(outputDirectory)

        when:
        CucumberJUnit5ParallelRunner.runFileFromClasspathInParallel("samples/simple_scenario.feature","net.serenitybdd.cucumber.integration.steps")
        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory).sort { it.name }
        def testOutcome = recordedTestOutcomes[0]

        then:
        testOutcome.result == TestResult.SUCCESS

        and:
        testOutcome.testSteps.collect { step -> step.result } == [TestResult.SUCCESS, TestResult.SUCCESS, TestResult.SUCCESS, TestResult.SUCCESS]
    }


}
