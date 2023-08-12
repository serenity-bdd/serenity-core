package net.thucydides.spock.acceptance

import net.thucydides.model.annotations.Step
import net.thucydides.core.annotations.Steps
import net.thucydides.model.domain.TestOutcome
import net.thucydides.core.steps.StepEventBus
import net.thucydides.spock.ThucydidesEnabled
import spock.lang.Specification

/**
 * Examples of running Thucydides-enabled specifications for non-webtest tests.
 */
class SimpleSteps {
    @Step
    def step1() {}

    @Step
    def step2() {}
}

@ThucydidesEnabled
class WhenRunningNonWebSpecifications extends Specification {

    @Steps
    SimpleSteps steps

    def "Instantiating step libraries in a Spock specification"() {
        given: "we want to generate Thucydides reports from a Spock specification"
        when: "we run the specification"
        then: "the @Steps field should have been instantiated"
            steps != null
    }

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
            println testOutcomes
            testOutcomes.collect {it.title} contains 'Should produce test outcomes'
    }

    def "Should produce test outcomes with the Given/When/Then steps at the top level"() {
        given: "we want to generate Thucydides reports from a Spock specification"
        when: "we use steps in the steps library"
            steps.step1()
            steps.step2()
        then: "the test outcome should record the executed steps"
            def thisTestOutcome = testOutcomes.find {it.title == 'Should produce test outcomes with the Given/When/Then steps at the top level'}
            thisTestOutcome.testSteps.collect { it.description } == ["Step1", "Step2"]
    }

    List<TestOutcome> getTestOutcomes() {
        StepEventBus.eventBus.baseStepListener.testOutcomes
    }
}
