package net.thucydides.core.matchers

import net.thucydides.core.model.Story
import net.thucydides.core.model.TestOutcome
import net.thucydides.core.model.TestResult
import net.thucydides.core.model.TestStep
import spock.lang.Specification

import static net.thucydides.core.matchers.PublicThucydidesMatchers.containsResults
import static org.hamcrest.MatcherAssert.assertThat

class WhenMatchingOutcomes extends Specification {

    static def successfulOutcome = TestOutcome.forTestInStory("someTest", Story.called("some story"))
    static def failedOutcome = TestOutcome.forTestInStory("someTest", Story.called("some story"))

    static {
        successfulOutcome.recordStep(TestStep.forStepCalled("step1").withResult(TestResult.SUCCESS))
        failedOutcome.recordStep(TestStep.forStepCalled("step1").withResult(TestResult.FAILURE))
    }

    def "should match outcome results"() {
        expect:
            assertThat(outcome, containsResults(expectedResult));
        where:
            outcome             | expectedResult
            successfulOutcome   | TestResult.SUCCESS
            failedOutcome       | TestResult.FAILURE
    }
}