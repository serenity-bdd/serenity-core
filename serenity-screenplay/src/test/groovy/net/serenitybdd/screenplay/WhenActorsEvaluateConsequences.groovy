package net.serenitybdd.screenplay
import net.serenitybdd.screenplay.shopping.DanaGoesShoppingSample
import net.serenitybdd.junit.runners.SerenityRunner
import org.junit.runner.notification.RunNotifier
import spock.lang.Specification

import static OutcomeChecks.resultsFrom
import static net.thucydides.core.model.TestResult.*

class WhenActorsEvaluateConsequences extends Specification{

    def "should produce a failed step when a single assumption fails"() {
        given:
            def runner = new SerenityRunner(DanaGoesShoppingSample)
        when:
            runner.run(new RunNotifier())
            def results = resultsFrom(runner.testOutcomes)
        then:
            def outcome = results["shouldBeAbleToPurchaseAnItemForFree"]
            outcome.result == FAILURE
            outcome.testSteps.collect { it.unrendered().description } == ["Given Dana has purchased an apple for 0 dollars",
                                                                          "Given Dana has purchased a pear for 5 dollars",
                                                                          "Then total cost should be (15)"]
            outcome.testSteps.collect { it.result } == [FAILURE, SKIPPED, SKIPPED]
    }

    def "should evaluate all the consequences in a group of consequences"() {
        given:
        def runner = new SerenityRunner(DanaGoesShoppingSample)
        when:
        runner.run(new RunNotifier())
        def results = resultsFrom(runner.testOutcomes)
        then:
        def outcome = results["shouldBeAbleToPurchaseAnItemWithAllTheRightDetails"]
        outcome.result == FAILURE
        outcome.testSteps.collect { it.result } == [SUCCESS, SUCCESS, FAILURE, FAILURE]
    }

    def "should skip the consequences following a failed group of consequences"() {
        given:
        def runner = new SerenityRunner(DanaGoesShoppingSample)
        when:
        runner.run(new RunNotifier())
        def results = resultsFrom(runner.testOutcomes)
        then:
        def outcome = results["shouldBeAbleToPurchaseAnItemWithAllTheRightDetailsAndContinue"]
        outcome.result == FAILURE
        outcome.testSteps.collect { it.result } == [SUCCESS, SUCCESS, FAILURE, SKIPPED]
    }

    def "should use all the consequences in a group of consequences to evaluate the total result"() {
        given:
        def runner = new SerenityRunner(DanaGoesShoppingSample)
        when:
        runner.run(new RunNotifier())
        def results = resultsFrom(runner.testOutcomes)
        then:
        def outcome = results["shouldBeAbleToPurchaseAnItemWithACompromisedTest"]
        outcome.testSteps.collect { it.result } == [SUCCESS, SUCCESS, FAILURE, COMPROMISED, FAILURE]
        outcome.result == COMPROMISED
    }

}
