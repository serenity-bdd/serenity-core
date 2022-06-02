package net.serenitybdd.cucumber.outcomes.junit5

import io.cucumber.junit.CucumberJUnit5Runner
import net.thucydides.core.guice.Injectors
import net.thucydides.core.reports.OutcomeFormat
import net.thucydides.core.reports.TestOutcomeLoader
import net.thucydides.core.webdriver.Configuration
import org.assertj.core.util.Files
import spock.lang.Specification


class WhenCreatingSerenityTestOutcomes_JU5 extends Specification {

    static File outputDirectory

    def setupSpec() {
        outputDirectory = Files.newTemporaryFolder()
    }

    def cleanupSpec() {
        outputDirectory.deleteDir()
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
        Configuration configuration = Injectors.getInjector().getProvider(Configuration.class).get();
        configuration.setOutputDirectory(outputDirectory);

        when:
        CucumberJUnit5Runner.runFileFromClasspath("samples/simple_scenario.feature","net.serenitybdd.cucumber.integration.steps");

        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory).sort { it.name };
        def testOutcome = recordedTestOutcomes[0]
        def steps = testOutcome.testSteps.collect { step -> step.description }

        then:
        testOutcome.title == "A simple scenario"
    }

}