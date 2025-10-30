package net.serenitybdd.junit.finder

import net.serenitybdd.core.di.SerenityInfrastructure
import net.serenitybdd.junit.runners.ParameterizedTestsOutcomeAggregator
import net.serenitybdd.junit.runners.SerenityParameterizedRunner
import net.thucydides.core.configuration.WebDriverConfiguration
import net.thucydides.core.webdriver.WebDriverFactory
import net.thucydides.junit.rules.QuietThucydidesLoggingRule
import net.thucydides.junit.rules.SaveWebdriverSystemPropertiesRule
import net.thucydides.model.ThucydidesSystemProperty
import net.thucydides.model.configuration.SystemPropertiesConfiguration
import net.thucydides.model.environment.MockEnvironmentVariables
import net.thucydides.model.webdriver.Configuration
import net.thucydides.samples.SampleDataDrivenScenarioWithQualifier
import org.junit.Rule
import org.junit.runner.notification.RunNotifier
import spock.lang.Specification

import java.nio.file.Files

class WhenRunningADataDrivenTestScenarioWithQualifier extends Specification {


    @Rule
    SaveWebdriverSystemPropertiesRule saveWebdriverSystemPropertiesRule = new SaveWebdriverSystemPropertiesRule()

    @Rule
    QuietThucydidesLoggingRule quietThucydidesLoggingRule = new QuietThucydidesLoggingRule()

    MockEnvironmentVariables environmentVariables

    Configuration configuration

    def setup() {
        environmentVariables = new MockEnvironmentVariables()
        configuration = new SystemPropertiesConfiguration(environmentVariables)
    }

    def "when test contains method to determine Qualifier it should be used for step names"() {
        given:
            def outputDirectory = Files.createTempDirectory("tmp").toFile()
        outputDirectory.deleteOnExit()

            environmentVariables.setProperty(ThucydidesSystemProperty.THUCYDIDES_OUTPUT_DIRECTORY.getPropertyName(),
                outputDirectory.getAbsolutePath())

            def runner = getStubbedTestRunnerUsing(SampleDataDrivenScenarioWithQualifier.class)
        when:
            runner.run(new RunNotifier())
            def outcomes = ParameterizedTestsOutcomeAggregator.from(runner).aggregateTestOutcomesByTestMethods()
        then:
            outcomes.size() == 1
            def happyDayOutcomes = outcomes.get(0)
        def happyDaySteps = happyDayOutcomes.getTestSteps()
            happyDaySteps.size() == 12
        and:
            happyDayOutcomes.getTitle().matches("Happy day scenario")
            happyDaySteps.each { step ->
                assert step.getDescription().matches("Happy day scenario for [a-zA-Z]{1} and \\d{1,2}")
            }
    }

    private SerenityParameterizedRunner getStubbedTestRunnerUsing(Class<?> testClass) throws Throwable {
        def configuration = new WebDriverConfiguration(environmentVariables)
        def factory = new WebDriverFactory(environmentVariables)
        def batchManager = SerenityInfrastructure.batchManager
        return new SerenityParameterizedRunner(testClass, configuration, factory, batchManager) {
            @Override
            void generateReports() {
                //do nothing
            }
        }
    }
}
