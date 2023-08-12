package net.thucydides.model.matchers

import net.thucydides.model.domain.Story
import net.thucydides.model.domain.TestOutcome
import net.thucydides.model.domain.TestResult
import net.thucydides.model.domain.TestStep
import spock.lang.Specification

import static net.thucydides.model.matchers.PublicThucydidesMatchers.containsResults
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
