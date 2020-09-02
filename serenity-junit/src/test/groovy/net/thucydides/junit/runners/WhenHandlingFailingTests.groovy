package net.thucydides.junit.runners

import net.serenitybdd.junit.runners.SerenityRunner
import net.thucydides.core.annotations.Step
import net.thucydides.core.annotations.Steps
import net.thucydides.core.model.TestResult
import net.thucydides.core.util.MockEnvironmentVariables
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.junit.runner.notification.RunNotifier
import spock.lang.Specification

//import org.openqa.selenium.firefox.FirefoxDriver
//import org.openqa.selenium.htmlunit.HtmlUnitDriver

class WhenHandlingFailingTests extends Specification {

//    def firefoxDriver = Mock(FirefoxDriver)
//    def htmlUnitDriver = Mock(HtmlUnitDriver)
    def environmentVariables = new MockEnvironmentVariables()
    File temporaryDirectory

    @Rule
    TemporaryFolder temporaryFolder

    def setup() {
        temporaryDirectory = temporaryFolder.newFolder()
    }


    static class SomeSteps {
        @Step
        public void myFailingStep() {
            throw new IllegalStateException()

        }

        @Step
        public void myUnexpectedlyFailingStep() {
            throw new UnknownError()

        }
    }

    @RunWith(SerenityRunner)
    static class ATestWithAnExpectedExceptionInAStep {

        @Steps
        SomeSteps mysteps;

        @Test(expected=IllegalStateException)
        public void shouldThrowAnIllegalStateException() {
            mysteps.myFailingStep();
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
        SomeSteps mysteps;

        @Test(expected=IllegalStateException)
        public void shouldThrowAnIllegalStateException() {
            mysteps.myUnexpectedlyFailingStep();
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
        public void shouldThrowAnIllegalStateException() {
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
        public void shouldThrowAnIllegalStateException() {
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
        public void shouldThrowAnIllegalStateException() {
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
