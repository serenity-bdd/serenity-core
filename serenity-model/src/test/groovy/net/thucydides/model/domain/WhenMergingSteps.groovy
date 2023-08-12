package net.thucydides.model.domain

import net.thucydides.model.domain.TestOutcome
import spock.lang.Specification
import spock.lang.Unroll

import static net.thucydides.model.domain.TestResult.*

class WhenMergingSteps extends Specification {

    def "Merging 0 steps makes no sense"() {
        given:
        TestOutcome outcome = new TestOutcome();
            outcome.recordStep(TestStep.forStepCalled("Step 1").withResult(SUCCESS))
            outcome.recordStep(TestStep.forStepCalled("Step 2").withResult(FAILURE))
            outcome.recordStep(TestStep.forStepCalled("Step 3").withResult(SKIPPED))
        when:
            outcome.mergeMostRecentSteps(0)
        then:
            thrown(IllegalArgumentException)
        and:
            outcome.testSteps.collect{ it.description } == ["Step 1", "Step 2", "Step 3"]
    }

    def "Merging 1 step should have no effect"() {
        given:
            TestOutcome outcome = new TestOutcome();
            outcome.recordStep(TestStep.forStepCalled("Step 1").withResult(SUCCESS))
            outcome.recordStep(TestStep.forStepCalled("Step 2").withResult(FAILURE))
            outcome.recordStep(TestStep.forStepCalled("Step 3").withResult(SKIPPED))
        when:
            outcome.mergeMostRecentSteps(1)
        then:
            outcome.testSteps.collect{ it.description } == ["Step 1", "Step 2", "Step 3"]
    }

    def "Merging 2 steps should include the previous step underneath the more recent one"() {
        given:
            TestOutcome outcome = new TestOutcome("Sample test");
            outcome.recordStep(TestStep.forStepCalled("Step 1").withResult(SUCCESS))
            outcome.recordStep(TestStep.forStepCalled("Step 2").withResult(FAILURE))
            outcome.recordStep(TestStep.forStepCalled("Step 3").withResult(SKIPPED))
        when:
            outcome.mergeMostRecentSteps(2)
        then:
            outcome.stepCount == 2 &&
            outcome.toString() == "Sample test:Step 1, Step 3 [Step 2]" &&
            outcome.testSteps[1].result == FAILURE
    }

    @Unroll
    def "Merging #previousResult and #nextResult should result in #mergedResult"() {
        given:
            TestOutcome outcome = new TestOutcome("Sample test");
            outcome.recordStep(TestStep.forStepCalled("Step 1").withResult(SUCCESS))
            outcome.recordStep(TestStep.forStepCalled("Step 2").withResult(previousResult))
            outcome.recordStep(TestStep.forStepCalled("Step 3").withResult(nextResult))
        when:
            outcome.mergeMostRecentSteps(2)
        then:
            outcome.stepCount == 2 &&
                    outcome.toString() == "Sample test:Step 1, Step 3 [Step 2]" &&
                    outcome.testSteps[1].result == mergedResult
        where:
            previousResult | nextResult | mergedResult
            FAILURE        | SKIPPED    | FAILURE
            FAILURE        | PENDING    | PENDING
            FAILURE        | IGNORED    | IGNORED
            FAILURE        | FAILURE    | FAILURE
            FAILURE        | ERROR      | ERROR

            ERROR          | SKIPPED    | ERROR
            ERROR          | PENDING    | PENDING
            ERROR          | IGNORED    | IGNORED
            ERROR          | FAILURE    | ERROR
    }
}
