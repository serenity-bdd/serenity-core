package net.serenitybdd.junit5



import net.thucydides.core.annotations.Steps
import net.thucydides.core.model.TestResult
import net.thucydides.core.steps.StepEventBus
import org.junit.Rule
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import spock.lang.Specification

import static org.junit.jupiter.api.Assertions.assertThrows

class WhenHandlingFailingTests extends Specification {

    static class SomeSteps {

        void myFailingStep() {
            throw new java.lang.IllegalStateException()
        }

        void myUnexpectedlyFailingStep() {
            throw new UnknownError()
        }
    }

    @ExtendWith(StepsInjectorTestInstancePostProcessor.class)
    static class ATestWithAnExpectedExceptionInAStep {

        @Steps
        SomeSteps mysteps;

        @Test
        void shouldThrowAnIllegalStateException() {
            assertThrows(java.lang.IllegalStateException.class) {
                mysteps.myFailingStep()
            };
        }
    }

    def "should report tests with an expected exception in a step as passing"() {
        given:
            def classToTest = ATestWithAnExpectedExceptionInAStep;
        when:
            TestLauncher.runTestForClass(classToTest);
        then:
            StepEventBus.getEventBus().getBaseStepListener().getTestOutcomes().get(0).result == TestResult.SUCCESS
    }

    @ExtendWith(StepsInjectorTestInstancePostProcessor.class)
    static class ATestWithAnUnexpectedExceptionInAStep {

        @Steps
        SomeSteps mysteps;

        @Test
        void shouldThrowAnIllegalStateException() {
            assertThrows(IllegalStateException.class) {
                mysteps.myUnexpectedlyFailingStep()
            }
        }
    }

    def "should report tests with an unexpected exception in a step as failing"() {
        given:
            def classToTest = ATestWithAnUnexpectedExceptionInAStep;
        when:
            TestLauncher.runTestForClass(classToTest);
        then:
            StepEventBus.getEventBus().getBaseStepListener().getTestOutcomes().get(0).result == TestResult.FAILURE
    }

    @ExtendWith(StepsInjectorTestInstancePostProcessor.class)
    static class ATestWithAnExpectedException {

        @Test
        void shouldThrowAnIllegalStateException() {
            assertThrows(IllegalStateException.class){ throw new IllegalStateException()};
        }
    }

    def "should report tests with an expected exception as passing"() {

        given:
            def classToTest = ATestWithAnExpectedException;
        when:
            TestLauncher.runTestForClass(classToTest);
        then:
            StepEventBus.getEventBus().getBaseStepListener().getTestOutcomes().get(0).result == TestResult.SUCCESS
    }


    static class ATestWithAnUnexpectedException {

        @Test
        public void shouldThrowAnIllegalStateException() {
            assertThrows(IllegalStateException.class) {
                throw new NullPointerException()
            }
        }
    }

    def "should report tests with an unexpected exception as failure"() {
        given:
            def classToTest = ATestWithAnUnexpectedException;
        when:
            TestLauncher.runTestForClass(classToTest);
        then:
            StepEventBus.getEventBus().getBaseStepListener().getTestOutcomes().get(0).result == TestResult.FAILURE
    }

    static class ATestWithAnUnexpectedFailure {

        @Test
        void shouldThrowAnIllegalStateException() {
            assertThrows(IllegalStateException.class,{-> throw new AssertionError(); });
        }
    }

    def "should report tests with an unexpected failure as failing"() {

        given:
            def classToTest = ATestWithAnUnexpectedFailure;
        when:
            TestLauncher.runTestForClass(classToTest);
        then:
            StepEventBus.getEventBus().getBaseStepListener().getTestOutcomes().get(0).result == TestResult.FAILURE

    }

}
