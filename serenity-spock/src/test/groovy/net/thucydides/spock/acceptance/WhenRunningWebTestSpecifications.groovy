package net.thucydides.spock.acceptance

import net.thucydides.core.annotations.Steps
import net.thucydides.core.pages.Pages
import net.thucydides.core.steps.ScenarioSteps
import net.thucydides.core.steps.StepEventBus
import net.thucydides.model.annotations.Step
import net.thucydides.model.domain.TestOutcome
import net.thucydides.spock.ThucydidesEnabled
import spock.lang.Specification

/**
 * Examples of running Thucydides-enabled specifications for non-webtest tests.
 */

class WebSteps extends ScenarioSteps {

    WebSteps(Pages pages) {
        super(pages)
    }

    @Step
    def step1() {}

    @Step
    def step2() {}
}

@ThucydidesEnabled(driver="chrome")
class WhenRunningWebTestSpecifications extends Specification {

    @Steps
    WebSteps steps

    def "Instantiating step libraries in a Spock specification"() {
        given: "we want to generate Thucydides reports from a webdriver-enabled Spock specification"
        when: "we run the specification using the 'driver' property in the @ThucydidesEnabled annotation"
        then: "the @Steps field should be instantiated"
            steps != null
        and: "the pages field of the step library should be instanciated"
            steps.pages != null
    }
    /*
    def "Should instantiate step libraries for data-driven tests"() {

        expect: "the @Steps field should have been instantiated"
            steps != null
         where:
            count << [1,2,3]
    }

    def "Should produce test outcomes"() {
        given: "we want to generate Thucydides reports from a Spock specification"
        when: "we run the specification"
        then: "a test outcome should have been produced"
            latestTestOutcomes.size() > 0
    }

    def "Should produce test outcomes with the Given/When/Then steps at the top level"() {
        given: "we want to generate Thucydides reports from a Spock specification"
        when: "we use steps in the steps library"
            steps.step1()
            steps.step2()
        then: "the test outcome should record the executed steps"
            latestTestOutcomes[0].testSteps.collect { it.description } == ["Step1", "Step2"]
    }
      */
    List<TestOutcome> getLatestTestOutcomes() {
        StepEventBus.eventBus.baseStepListener.testOutcomes
    }
}
