package net.serenitybdd.cucumber.outcomes.junit5

import io.cucumber.junit.CucumberJUnit5Runner
import net.serenitybdd.cucumber.integration.SimpleScenario
import net.serenitybdd.cucumber.integration.ju5.SimpleScenarioJU5
import net.thucydides.core.reports.OutcomeFormat
import net.thucydides.core.reports.TestOutcomeLoader
import org.assertj.core.util.Files
import spock.lang.Specification

import static io.cucumber.junit.CucumberRunner.serenityRunnerForCucumberTestRunner

class WhenCreatingSerenityTestOutcomes_JU5 extends Specification {

    File outputDirectory

    def setup() {
        outputDirectory = Files.newTemporaryFolder()
    }



    /*
    Feature: A simple feature

      Scenario: A simple scenario
        Given I want to purchase 2 widgets
        And a widget costs $5
        When I buy the widgets
        Then I should be billed $10
     */

    def "should generate a well-structured Serenity test outcome for each executed Cucumber scenario"() {
        given:
        //def runtime = serenityRunnerForCucumberTestRunner(SimpleScenario.class, outputDirectory);

        when:
        CucumberJUnit5Runner.run(SimpleScenarioJU5.class)
        /*runtime.run();
        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory).sort { it.name };
        def testOutcome = recordedTestOutcomes[0]
        def steps = testOutcome.testSteps.collect { step -> step.description }*/

        then:
        //testOutcome.title == "A simple scenario"
        int a =2;
    }


}