package net.serenitybdd.screenplay

import net.serenitybdd.junit.runners.SerenityRunner
import net.serenitybdd.screenplay.shopping.DanaGoesShoppingSample
import net.serenitybdd.screenplay.shopping.DanaGoesShoppingQuicklySample
import net.thucydides.model.domain.TestResult
import org.junit.runner.notification.RunNotifier
import spock.lang.Specification

import static OutcomeChecks.resultsFrom
import static net.thucydides.model.domain.TestResult.*

class WhenActorsGoOnAJourney extends Specification{

    def danaGoesShoppingSample

    def setup() {
        danaGoesShoppingSample = new SerenityRunner(DanaGoesShoppingQuicklySample)
    }


    def "should produce a normal test outcome"() {
        when:
            danaGoesShoppingSample.run(new RunNotifier())
            def results = resultsFrom(danaGoesShoppingSample.testOutcomes)
        then:
            !results.empty
        and:
            def outcome = results["shouldBeAbleToPurchaseSomeItems"]
            outcome.title == "Should be able to purchase some items"
            outcome.result == TestResult.SUCCESS
    }

    def "should produce a step for each task call"() {
        when:
            danaGoesShoppingSample.run(new RunNotifier())
            def results = resultsFrom(danaGoesShoppingSample.testOutcomes)
        then:
            def outcome = results["shouldBeAbleToPurchaseSomeItems"]
            outcome.testSteps.collect { it.unrendered().description } == ["Given Dana has purchased an apple for 10 dollars",
                                                                          "Given Dana has purchased a pear for 5 dollars",
                                                                          "And Dana has them delivered"]
    }

    def "should produce a failed step when an assumption fails"() {
        when:
            danaGoesShoppingSample.run(new RunNotifier())
            def results = resultsFrom(danaGoesShoppingSample.testOutcomes)
        then:
            def outcome = results["shouldBeAbleToPurchaseAnItemForFree"]
            outcome.result == FAILURE
            outcome.testSteps.collect { it.unrendered().description } == ["Given Dana has purchased an apple for 0 dollars",
                                                                          "Given Dana has purchased a pear for 5 dollars"]
            outcome.testSteps.collect { it.result } == [FAILURE, SKIPPED]
    }

    def "should produce a step with an error when a step breaks"() {
        when:
            danaGoesShoppingSample.run(new RunNotifier())
            def results = resultsFrom(danaGoesShoppingSample.testOutcomes)
        then:
            def outcome = results["shouldBeAbleToPurchaseAnItemWithANegativeAmount"]
            outcome.result == ERROR
            outcome.testSteps.collect { it.result } == [ERROR, SKIPPED]
    }

    def "should document tasks as far as possible when errors occur"() {
        when:
            danaGoesShoppingSample.run(new RunNotifier())
            def results = resultsFrom(danaGoesShoppingSample.testOutcomes)
        then:
            def outcome = results["shouldBeAbleToPurchaseAnItemWithANegativeAmount"]
            outcome.testSteps.collect { it.unrendered().description } == ["Given Dana has purchased an apple for -10 dollars",
                                                                          "Given Dana has purchased a pear for 5 dollars"]
    }

    def "should not evaluate consequences after a failed step"() {
        when:
        danaGoesShoppingSample.run(new RunNotifier())
        def results = resultsFrom(danaGoesShoppingSample.testOutcomes)
        then:
        def outcome = results["shouldBeAbleToPurchaseAnItemForFree"]
        outcome.result == FAILURE
        outcome.testSteps.collect { it.result } == [FAILURE, SKIPPED]
    }
}
