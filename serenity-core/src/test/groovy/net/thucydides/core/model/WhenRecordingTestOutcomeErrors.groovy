package net.thucydides.core.model

import net.thucydides.core.webdriver.exceptions.ElementShouldBePresentException
import spock.lang.Specification


class WhenRecordingTestOutcomeErrors extends Specification {

    def "should record failure classname and message"() {

        given:
        def testOutcome = TestOutcome.forTestInStory("aTest", Story.called("a story"))

        when:
        testOutcome.determineTestFailureCause(new AssertionError("oh crap!"))

        then:
        testOutcome.testFailureCause.errorType == "java.lang.AssertionError"
        testOutcome.testFailureClassname == "java.lang.AssertionError"
        testOutcome.testFailureMessage == "oh crap!"

        testOutcome.errorMessage.contains "oh crap!"
        testOutcome.result == TestResult.FAILURE
    }

    def "should record failure message in steps"() {

        given:
        def testOutcome = TestOutcome.forTestInStory("aTest", Story.called("a story"))

        when:
        def failingStep = TestStep.forStepCalled("failing step").withResult(TestResult.FAILURE)
        failingStep.failedWith(new AssertionError("oh crap!"))

        testOutcome.recordStep(failingStep)

        then:
        testOutcome.errorMessage.contains "oh crap!"
        testOutcome.result == TestResult.FAILURE
    }

    def "should record error classname and message"() {

        given:
        def testOutcome = TestOutcome.forTestInStory("aTest", Story.called("a story"))

        when:
        testOutcome.determineTestFailureCause(new RuntimeException("oh crap!"))

        then:
        testOutcome.testFailureClassname == "java.lang.RuntimeException"
        testOutcome.testFailureMessage == "oh crap!"

        testOutcome.result == TestResult.ERROR
        testOutcome.errorMessage.contains "oh crap!"
    }

    def "should record error message in steps"() {

        given:
        def testOutcome = TestOutcome.forTestInStory("aTest", Story.called("a story"))

        when:
        def failingStep = TestStep.forStepCalled("failing step").withResult(TestResult.ERROR)
        failingStep.failedWith(new RuntimeException("oh crap!"))

        testOutcome.recordStep(failingStep)

        then:
        testOutcome.errorMessage.contains "oh crap!"
        testOutcome.result == TestResult.ERROR
    }

    def "should-style assertions should be marked as failures"() {

        given:
        def testOutcome = TestOutcome.forTestInStory("aTest", Story.called("a story"))

        when:
        testOutcome.determineTestFailureCause(new ElementShouldBePresentException("oh crap!",new RuntimeException("crapity crap crap!")))

        then:
        testOutcome.testFailureClassname == "net.thucydides.core.webdriver.exceptions.ElementShouldBePresentException"
        testOutcome.testFailureMessage.contains "oh crap!"

        testOutcome.result == TestResult.FAILURE
        testOutcome.errorMessage.contains "oh crap!"
    }
}