package net.thucydides.core.steps

import net.thucydides.core.annotations.Steps
import net.thucydides.model.environment.MockEnvironmentVariables
import spock.lang.Specification

class WhenUsingMultipleInstancesOfTheSameStepsLibraryInATest extends Specification {

    static class SampleStepLibrary {

    }

    static class SimpleTestSampleWithDefaultSteps {
        @Steps
        SampleStepLibrary steps1

        @Steps
        SampleStepLibrary steps2
    }

    static class SimpleTestSampleSupportingMultipleInstances {
        @Steps
        SampleStepLibrary steps1

        @Steps
        SampleStepLibrary steps2

        @Steps(uniqueInstance=true)
        SampleStepLibrary uniqueInstanceSteps1

        @Steps(uniqueInstance=true)
        SampleStepLibrary uniqueInstanceSteps2

    }

    static class SimpleTestSampleSupportingSharedInstances {
        @Steps
        SampleStepLibrary steps1

        @Steps
        SampleStepLibrary steps2

        @Steps(shared=true)
        SampleStepLibrary sharedSteps1

        @Steps(shared=true)
        SampleStepLibrary sharedSteps2

    }
    def stepFactory = new StepFactory()
    def environmentVariables = new MockEnvironmentVariables()

    def "Should instantiate a new singlton step library for each @Steps annotation by default"() {
        given:
            def testCase = new SimpleTestSampleWithDefaultSteps()
        when: "we get two instances of the same step library"
            StepAnnotations.withEnvironmentVariables(environmentVariables).injectScenarioStepsInto(testCase, stepFactory)
        then: "the step libraries should be instantiated"
            testCase.steps1 != null && testCase.steps2 != null
        and: "by default different instances should be used"
            testCase.steps1 != testCase.steps2
    }

    def "Should instantiate a shared singlton step library for each @Steps annotation in legacy mode"() {
        given:
            def testCase = new SimpleTestSampleSupportingMultipleInstances()
        when: "we get two instances of the same step library"
            environmentVariables.setProperty("step.creation.strategy","legacy")
        and:
            StepAnnotations.withEnvironmentVariables(environmentVariables).injectScenarioStepsInto(testCase, stepFactory)
        then: "the step libraries should be instantiated"
            testCase.steps1 != null && testCase.steps2 != null
        and: "the same shared instance should be used"
            testCase.steps1 == testCase.steps2
    }

    def "Should instantiate different step library instances in legacy mode when 'uniqueInstance' is used"() {
        given:
            def testCase = new SimpleTestSampleSupportingMultipleInstances()
        when: "we get two instances of the same step library"
            environmentVariables.setProperty("step.creation.strategy","legacy")
        and:
            StepAnnotations.withEnvironmentVariables(environmentVariables).injectScenarioStepsInto(testCase, stepFactory)
        then:
            testCase.uniqueInstanceSteps1 != null && testCase.uniqueInstanceSteps2 != null
        and: "different instances should be used"
            testCase.uniqueInstanceSteps1 != testCase.uniqueInstanceSteps2
    }

    def "Should instantiate a new step libraries for each @Steps annotation in default mode"() {
        given:
            def testCase = new SimpleTestSampleSupportingSharedInstances()
        when: "we get two instances of the same step library"
            environmentVariables.setProperty("step.creation.strategy","default")
        and:
            StepAnnotations.withEnvironmentVariables(environmentVariables).injectScenarioStepsInto(testCase, stepFactory)
        then: "the step libraries should be instantiated"
            testCase.steps1 != null && testCase.steps2 != null
        and: "the different instances should be used"
            testCase.steps1 != testCase.steps2
    }

    def "Should instantiate shared step libraries for each shared @Steps annotation in default mode"() {
        given:
            def testCase = new SimpleTestSampleSupportingSharedInstances()
        when: "we get two instances of the same step library"
            environmentVariables.setProperty("step.creation.strategy","default")
        and:
            StepAnnotations.withEnvironmentVariables(environmentVariables).injectScenarioStepsInto(testCase, stepFactory)
        then: "the step libraries should be instantiated"
            testCase.sharedSteps1 != null && testCase.sharedSteps2 != null
        and: "the different instances should be used"
            testCase.sharedSteps1 == testCase.sharedSteps2
    }
}
