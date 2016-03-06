package net.thucydides.core.steps

import net.thucydides.core.annotations.Steps
import spock.lang.Specification

import static net.thucydides.core.steps.StepAnnotations.injectScenarioStepsInto

public class WhenUsingMultipleInstancesOfTheSameStepsLibraryInATest extends Specification {

    static class SampleStepLibrary {

    }

    static class SimpleTestSampleWithDefaultSteps {
        @Steps
        SampleStepLibrary steps1;

        @Steps
        SampleStepLibrary steps2
    }

    static class SimpleTestSampleSupportingMultipleInstances {
        @Steps
        SampleStepLibrary steps1;

        @Steps
        SampleStepLibrary steps2;

        @Steps(uniqueInstance=true)
        SampleStepLibrary steps3

        @Steps(uniqueInstance=true)
        SampleStepLibrary steps4

    }

    def "should instantiate a new singlton step library for each @Steps annotation by default"() {
        given:
            def stepFactory = new StepFactory()
            def testCase = new SimpleTestSampleSupportingMultipleInstances()
        when: "we get two instances of the same step library"
            injectScenarioStepsInto(testCase, stepFactory);
            def stepLibrary1 = testCase.steps1
            def stepLibrary2 = testCase.steps2
        then: "the step libraries should be instantiated"
            stepLibrary1 != null && stepLibrary2 != null
        then: "by default the same instance should be used"
            stepLibrary1 == stepLibrary2
    }

    def "should be able to instantiate multiple instances of the same step library class"() {
        given:
            def stepFactory = new StepFactory()
        when: "we get two instances of the same step library"
            def testCase = new SimpleTestSampleSupportingMultipleInstances()
            injectScenarioStepsInto(testCase, stepFactory);
        then: "the step libraries should be instantiated"
            testCase.steps1 != null && testCase.steps2 != null && testCase.steps3 != null
        then: "by default the same instance should be used"
            testCase.steps1 == testCase.steps2
        and: "a new instance should be used if specified"
            testCase.steps3 != testCase.steps2 && testCase.steps3 != testCase.steps1
            testCase.steps4 != testCase.steps3 && testCase.steps4 != testCase.steps1
    }
}
