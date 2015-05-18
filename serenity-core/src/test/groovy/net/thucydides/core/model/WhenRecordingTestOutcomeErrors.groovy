package net.thucydides.core.model

import spock.lang.Specification


/**
 * Created by john on 12/06/2014.
 */
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

        testOutcome.errorMessage == "oh crap!"
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
        testOutcome.errorMessage == "oh crap!"
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
        testOutcome.errorMessage == "oh crap!"
    }

    def "should record error message in steps"() {

        given:
        def testOutcome = TestOutcome.forTestInStory("aTest", Story.called("a story"))

        when:
        def failingStep = TestStep.forStepCalled("failing step").withResult(TestResult.ERROR)
        failingStep.failedWith(new RuntimeException("oh crap!"))

        testOutcome.recordStep(failingStep)

        then:
        testOutcome.errorMessage == "oh crap!"
        testOutcome.result == TestResult.ERROR
    }
}