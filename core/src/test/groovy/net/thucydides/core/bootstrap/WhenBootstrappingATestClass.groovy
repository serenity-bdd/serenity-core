package net.thucydides.core.bootstrap

import com.google.common.base.Optional
import net.thucydides.core.annotations.Step
import net.thucydides.core.annotations.Steps
import net.thucydides.core.steps.StepEventBus
import spock.lang.Specification

/**
 * How to enrich a test class for use with Thucydides.
 * This is designed to be an API that can be used for more standardized integration with other test libraries.
 */
class WhenBootstrappingATestClass extends Specification {

    static class StepLibrary {
        @Step
        def step1() {}

        @Step
        def step2() {}
    }

    static class SampleTestClass {
        @Steps
        StepLibrary steps

        public void testSomething() {
            steps.step1();
            steps.step2();
        }
    }

    def "should instantiate classes annotated with the @Step annotation"() {
        given: "a test class object with a field annotated with @Step"
            def testCase = new SampleTestClass()
        when: "we enrich the object"
            new ThucydidesAgent(Optional.absent()).enrich(testCase)
        then: "the step field should have been instantiated"
            testCase.steps != null
    }

    def "should register a listener to record @Step-annotated method calls in the test results"() {
        given: "a test class object with a field annotated with @Step"
            def testCase = new SampleTestClass()
        when: "we enrich the object"
            new ThucydidesAgent(Optional.absent()).enrich(testCase)
        and: "we start the test"
            StepEventBus.getEventBus().testStarted("Test Something");
        and: "we call a @Step-annotated method in the step library"
            testCase.testSomething()
        then: "the method should have been recorded in the test result"
            latestTestOutcome.methodName == 'Test Something'
            latestTestOutcome.testSteps.collect {it.description} == ["Step1","Step2"]
    }

    def "should generate reports when the tests are finished"() {
        given: "a test class object with a field annotated with @Step"
            def testCase = new SampleTestClass()
        when: "we run the test"
            new ThucydidesAgent(Optional.absent()).enrich(testCase)
            StepEventBus.getEventBus().testStarted("Test Something");
            testCase.testSomething()
        then:
            true
    }

    def getLatestTestOutcome() {
        ThucydidesContext.currentContext.stepListener.testOutcomes[0]
    }

}
