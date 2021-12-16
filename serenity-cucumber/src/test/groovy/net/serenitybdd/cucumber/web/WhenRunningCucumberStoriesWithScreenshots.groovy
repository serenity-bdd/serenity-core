package net.serenitybdd.cucumber.web

import net.serenitybdd.cucumber.integration.PassingWebTestSampleWithNestedSteps
import net.serenitybdd.cucumber.integration.SimpleSeleniumFailingAndPassingScenario
import net.serenitybdd.cucumber.integration.SimpleSeleniumSeveralScenarios
import net.thucydides.core.model.TestOutcome
import net.thucydides.core.model.TestStep
import net.thucydides.core.reports.OutcomeFormat
import net.thucydides.core.reports.TestOutcomeLoader
import net.thucydides.core.util.MockEnvironmentVariables
import spock.lang.Specification

import java.nio.file.Files

import static io.cucumber.junit.CucumberRunner.serenityRunnerForCucumberTestRunner

class WhenRunningCucumberStoriesWithScreenshots extends Specification {


    File outputDirectory

    def environmentVariables = new MockEnvironmentVariables()

    def setup() {
        outputDirectory = Files.createTempDirectory("cukes").toFile();
        outputDirectory.deleteOnExit();

        environmentVariables.setProperty("webdriver.driver", "chrome")
    }


    def "web tests should take screenshots"() {
        given:
        def runtime = serenityRunnerForCucumberTestRunner(SimpleSeleniumFailingAndPassingScenario, outputDirectory, environmentVariables);

        when:
        runtime.run();
        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory);

        then:
        TestStep given = givenStepIn(recordedTestOutcomes);
        given.getScreenshots().size() > 0
    }

    def "web tests should take screenshots with multiple scenarios"() {

        given:
        def runtime = serenityRunnerForCucumberTestRunner(SimpleSeleniumSeveralScenarios, outputDirectory, environmentVariables);

        when:
        runtime.run();
        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory);

        then:
        TestStep given = givenStepIn(recordedTestOutcomes);
        given.getScreenshots().size() > 0
    }

    def "web tests should take screenshots for multiple tests"()  {

        given:
        def runtime = serenityRunnerForCucumberTestRunner(SimpleSeleniumSeveralScenarios, outputDirectory, environmentVariables);

        when:
        runtime.run();
        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory);

        then:
        TestStep given1 = givenStepIn(recordedTestOutcomes,0);
        TestStep given2 = givenStepIn(recordedTestOutcomes,1);

        given1.getScreenshots().size() > 0
        given2.getScreenshots().size() > 0
    }


    def "web tests should take screenshots with nested step libraries"()  {

        given:
        def runtime = serenityRunnerForCucumberTestRunner(PassingWebTestSampleWithNestedSteps, outputDirectory, environmentVariables);

        when:
        runtime.run();
        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory);

        then:
        TestStep given = givenStepIn(recordedTestOutcomes,0);

        given.getScreenshots().size() > 0

    }

    protected TestStep givenStepIn(List<TestOutcome> outcomes) {
        return givenStepIn(outcomes,0);
    }

    protected TestStep givenStepIn(List<TestOutcome> outcomes, int index) {
        TestStep givenStep = outcomes.get(index).getTestSteps().get(0);
        if (!givenStep.getDescription().startsWith("Given")) {
            givenStep = givenStep.getChildren().get(0);
        }
        return givenStep;
    }

   /*

    @Test
    public void web_tests_should_take_screenshots_for_multiple_tests() throws Throwable {

        // Given
        ThucydidesJUnitStories story = newStory("*PassingBehaviorWithSeleniumAndSeveralScenarios.story");

        // When
        run(story);

        // Then
        List<TestOutcome> outcomes = loadTestOutcomes();

        TestStep given1 = givenStepIn(outcomes,0);
        TestStep given2 = givenStepIn(outcomes,1);
        TestStep given3 = givenStepIn(outcomes,2);
        TestStep given4 = givenStepIn(outcomes,3);

        assertThat(given1.getScreenshots().size(), greaterThan(0));
        assertThat(given2.getScreenshots().size(), greaterThan(0));
        assertThat(given3.getScreenshots().size(), greaterThan(0));
        assertThat(given4.getScreenshots().size(), greaterThan(0));
    }                                                */

    //@Test
    //public void web_tests_should_take_screenshots_with_nested_step_libraries() throws Throwable {

        // Given
     //   ThucydidesJUnitStories story = newStory("**/aPassingWebTestSampleWithNestedSteps.story");
     //   story.setEnvironmentVariables(environmentVariables);

        // When
       // run(story);

        // Then
       // List<TestOutcome> outcomes = loadTestOutcomes();
       // TestStep given = givenStepIn(outcomes);
       // assertThat(given.getScreenshots().size(), greaterThan(0));

    //}
}
