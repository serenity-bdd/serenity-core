package net.serenitybdd.cucumber.web


import net.serenitybdd.cucumber.integration.*
import net.thucydides.core.ThucydidesSystemProperty
import net.thucydides.core.configuration.SystemPropertiesConfiguration
import net.thucydides.core.model.TestResult
import net.thucydides.core.reports.OutcomeFormat
import net.thucydides.core.reports.TestOutcomeLoader
import net.thucydides.core.util.MockEnvironmentVariables
import net.thucydides.core.webdriver.Configuration
import org.assertj.core.util.Files
import spock.lang.Specification

import static io.cucumber.junit.CucumberRunner.serenityRunnerForCucumberTestRunner

class WhenRunningWebCucumberStories extends Specification {

    File outputDirectory

    def environmentVariables = new MockEnvironmentVariables()

    def setup() {
        outputDirectory = Files.newTemporaryFolder()
    }

    def "should run table-driven scenarios successfully"() {
        given:
        def runtime = serenityRunnerForCucumberTestRunner(SimpleSeleniumScenario.class, outputDirectory, environmentVariables);

        when:
        runtime.run();
        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory);
        def testOutcome = recordedTestOutcomes[0]

        then:
        testOutcome.title == "A scenario that uses selenium"
        testOutcome.tags
        and: "there should be one step for each row in the table"
        testOutcome.stepCount == 2
    }


    def "a failing story should generate failure test outcome"() throws Throwable {
        given:
        def runtime = serenityRunnerForCucumberTestRunner(SimpleSeleniumFailingScenario.class, outputDirectory, environmentVariables);

        when:
        runtime.run();
        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory);
        def testOutcome = recordedTestOutcomes[0]


        then:
        testOutcome.title == "A failing scenario that uses selenium"
        testOutcome.isFailure();

        and: "there should be one step for each row in the table"
        testOutcome.stepCount == 2
    }



    def "a test should use a different browser if requested"()  {
        given:
        def runtime = serenityRunnerForCucumberTestRunner(SimpleSeleniumDifferentBrowserScenario.class, outputDirectory);

        when:
        runtime.run();
        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory);
        def testOutcome = recordedTestOutcomes[0]

        then:
        testOutcome.title == "A scenario that uses selenium"
        testOutcome.isSuccess();

        and: "there should be one step for each row in the table"
        testOutcome.stepCount == 2
    }


    def "a cucumber step library can use page objects directly"()  {
        given:
        def runtime = serenityRunnerForCucumberTestRunner(SimpleSeleniumPageObjects.class, outputDirectory, environmentVariables);

        when:
        runtime.run();
        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory);
        def testOutcome = recordedTestOutcomes[0]

        then:
        testOutcome.title == "A scenario that uses selenium"
        testOutcome.isSuccess();

        and: "there should be one step for each row in the table"
        testOutcome.stepCount == 2
    }


    def "stories with errors in one scenario should still run subsequent scenarios"()  {
        given:
        environmentVariables.setProperty("restart.browser.each.scenario","true");
        def runtime = serenityRunnerForCucumberTestRunner(SimpleSeleniumFailingAndPassingScenario, outputDirectory, environmentVariables);

        when:
        runtime.run();
        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory).sort{it.startTime}

        then:
        recordedTestOutcomes.size() == 2
        recordedTestOutcomes[0].result == TestResult.FAILURE
        recordedTestOutcomes[1].result == TestResult.SUCCESS
    }


    def "should be able to specify the browser in the base test"() {
        given:
        environmentVariables.setProperty(ThucydidesSystemProperty.DRIVER.getPropertyName(), "htmlunit");
        environmentVariables.setProperty(ThucydidesSystemProperty.THUCYDIDES_USE_UNIQUE_BROWSER.getPropertyName(),"true");
        def runtime = serenityRunnerForCucumberTestRunner(SimpleSeleniumFailingAndPassingScenario, outputDirectory, environmentVariables);

        when:
        runtime.run();
        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory).sort{it.startTime};

        then:
        recordedTestOutcomes[0].result == TestResult.FAILURE
    }


    def "should be able to set thucydides properties in the base test"() {
        given:
        environmentVariables.setProperty(ThucydidesSystemProperty.WEBDRIVER_BASE_URL.getPropertyName(),"some-base-url")
        Configuration systemConfiguration = new SystemPropertiesConfiguration(environmentVariables);
        systemConfiguration.setOutputDirectory(outputDirectory);
        def runtime = serenityRunnerForCucumberTestRunner(SimpleSeleniumFailingAndPassingScenario, systemConfiguration)

        when:
        runtime.run();

        then:
        systemConfiguration.getBaseUrl() == "some-base-url"

    }



    def  "data  driven  steps  should  appear  as  nested  steps"()  {
        given:
        def runtime = serenityRunnerForCucumberTestRunner(DataDrivenScenario.class, outputDirectory);

        when:
        runtime.run();
        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory);

        then:
        recordedTestOutcomes.size() == 1
        def topLevelSteps = recordedTestOutcomes.get(0).getTestSteps()
        topLevelSteps.size() == 3

        def nestedDataDrivenSteps = topLevelSteps.get(2).getChildren().get(0).getChildren();
        nestedDataDrivenSteps.size() == 3;
    }



    def "two  scenarios  using  the  same  given  story  should  return  two  test  outcomes"() {
        given:
        def runtime = serenityRunnerForCucumberTestRunner(ScenarioSuite.class, outputDirectory);

        when:
        runtime.run();
        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory);

        then:
        recordedTestOutcomes.size() == 2
    }
}
