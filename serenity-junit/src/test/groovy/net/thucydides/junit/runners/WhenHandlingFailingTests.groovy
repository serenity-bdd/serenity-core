package net.thucydides.junit.runners

import net.serenitybdd.junit.runners.SerenityRunner
import net.serenitybdd.annotations.Step
import net.serenitybdd.annotations.Steps
import net.thucydides.model.domain.TestResult
import net.thucydides.model.environment.MockEnvironmentVariables
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runner.notification.RunNotifier
import spock.lang.Specification

import java.nio.file.Files

class WhenHandlingFailingTests extends Specification {

    File temporaryDirectory

    def setup() {
        temporaryDirectory = Files.createTempDirectory("tmp").toFile()
        temporaryDirectory.deleteOnExit()
    }


    static class SomeSteps {
        @Step
        void myFailingStep() {
            throw new IllegalStateException()

        }

        @Step
        void myUnexpectedlyFailingStep() {
            throw new UnknownError()

        }
    }

    @RunWith(SerenityRunner)
    static class ATestWithAnExpectedExceptionInAStep {

        @Steps
        SomeSteps mysteps

        @Test(expected=IllegalStateException)
        void shouldThrowAnIllegalStateException() {
            try {
                mysteps.myFailingStep()
            } catch(Exception e) {
                System.out.println("ZZZThrowsException " + e)
            }
        }
    }

    def "should report tests with an expected exception in a step as passing"() {
        given:
        def runner = new SerenityRunner(ATestWithAnExpectedExceptionInAStep)
        when:
        runner.run(new RunNotifier())
        then:
        runner.testOutcomes.get(0).result == TestResult.SUCCESS
    }

    @RunWith(SerenityRunner)
    static class ATestWithAnUnexpectedExceptionInAStep {

        @Steps
        SomeSteps mysteps

        @Test(expected=IllegalStateException)
        void shouldThrowAnIllegalStateException() {
            mysteps.myUnexpectedlyFailingStep()
        }
    }

    def "should report tests with an unexpected exception in a step as failing"() {
        given:
            def runner = new SerenityRunner(ATestWithAnUnexpectedExceptionInAStep)
        when:
            runner.run(new RunNotifier())
        then:
            runner.testOutcomes.get(0).result == TestResult.ERROR
    }

    @RunWith(SerenityRunner)
    static class ATestWithAnExpectedException {

        @Test(expected=IllegalStateException)
        void shouldThrowAnIllegalStateException() {
            throw new IllegalStateException()
        }
    }

    def "should report tests with an expected exception as passing"() {
        given:
            def runner = new SerenityRunner(ATestWithAnExpectedException)
        when:
            runner.run(new RunNotifier())
        then:
            runner.testOutcomes.get(0).result == TestResult.SUCCESS
    }

    @RunWith(SerenityRunner)
    static class ATestWithAnUnexpectedException {

        @Test(expected=IllegalStateException)
        void shouldThrowAnIllegalStateException() {
            throw new NullPointerException()
        }
    }

    def "should report tests with an unexpected exception as errors"() {
        given:
            def runner = new SerenityRunner(ATestWithAnUnexpectedException)
        when:
            runner.run(new RunNotifier())
        then:
            runner.testOutcomes.get(0).result == TestResult.ERROR
    }

    @RunWith(SerenityRunner)
    static class ATestWithAnUnexpectedFailure {

        @Test(expected=IllegalStateException)
        void shouldThrowAnIllegalStateException() {
            throw new AssertionError()
        }
    }

    def "should report tests with an unexpected failure as failing"() {
        given:
            def runner = new SerenityRunner(ATestWithAnUnexpectedFailure)
        when:
            runner.run(new RunNotifier())
        then:
            runner.testOutcomes.get(0).result == TestResult.FAILURE
    }

}
