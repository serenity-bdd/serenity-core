package net.serenitybdd.journey
import net.serenitybdd.journey.shopping.DanaGoesShoppingSample
import net.serenitybdd.junit.runners.SerenityRunner
import net.thucydides.core.model.TestResult
import org.junit.runner.notification.RunNotifier
import spock.lang.Specification

import static OutcomeChecks.resultsFrom
import static net.thucydides.core.model.TestResult.*

class WhenActorsGoOnAJourney extends Specification{

    def "should produce a normal test outcome"() {
        given:
            def runner = new SerenityRunner(DanaGoesShoppingSample)
        when:
            runner.run(new RunNotifier())
            def results = resultsFrom(runner.testOutcomes)
        then:
            !results.empty
        and:
            def outcome = results["shouldBeAbleToPurchaseSomeItems"]
            outcome.title == "Should be able to purchase some items"
            outcome.result == TestResult.SUCCESS
    }

    def "should produce a step for each task call"() {
        given:
            def runner = new SerenityRunner(DanaGoesShoppingSample)
        when:
            runner.run(new RunNotifier())
            def results = resultsFrom(runner.testOutcomes)
        then:
            def outcome = results["shouldBeAbleToPurchaseSomeItems"]
            outcome.testSteps.collect { it.unrendered().description } == ["Given Dana has purchased an apple for 10 dollars",
                                                                          "Given Dana has purchased a pear for 5 dollars",
                                                                          "And Dana has them delivered"]
    }

    def "should produce a failed step when an assumption fails"() {
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
                                                                          "Then total cost should be <15>"]
            outcome.testSteps.collect { it.result } == [FAILURE, SKIPPED, SKIPPED]
    }

    def "should produce a step with an error when a step breaks"() {
        given:
            def runner = new SerenityRunner(DanaGoesShoppingSample)
        when:
            runner.run(new RunNotifier())
            def results = resultsFrom(runner.testOutcomes)
        then:
            def outcome = results["shouldBeAbleToPurchaseAnItemWithANegativeAmount"]
            outcome.result == ERROR
            outcome.testSteps.collect { it.result } == [ERROR, SKIPPED]
    }

    def "should document tasks as far as possible when errors occur"() {
        given:
            def runner = new SerenityRunner(DanaGoesShoppingSample)
        when:
            runner.run(new RunNotifier())
            def results = resultsFrom(runner.testOutcomes)
        then:
            def outcome = results["shouldBeAbleToPurchaseAnItemWithANegativeAmount"]
            outcome.testSteps.collect { it.unrendered().description } == ["Given Dana has purchased an apple for -10 dollars",
                                                                          "Given Dana has purchased a pear for 5 dollars"]
    }

    def "should report failing expectations correctly"() {
        given:
            def runner = new SerenityRunner(DanaGoesShoppingSample)
        when:
            runner.run(new RunNotifier())
            def results = resultsFrom(runner.testOutcomes)
        then:
            def outcome = results["shouldBeAbleToPurchaseSomeItemsWithDelivery"]
            outcome.result == FAILURE
        and:
            outcome.testSteps.collect { it.unrendered().description } ==
                    ["Given Dana has purchased an apple for 10 dollars",
                     "Given Dana has purchased a pear for 5 dollars",
                     "And Dana has them delivered",
                     "Then total cost should be <15>",
                     "Then total cost including delivery should be a value equal to or greater than <20>",
                      "Then thank you message should be \"Thank you!\""]
        and:
            outcome.testSteps[3].result == FAILURE && outcome.testSteps[4].result == SKIPPED

    }

    def "should not evaluate consequences after a failed step"() {
        given:
        def runner = new SerenityRunner(DanaGoesShoppingSample)
        when:
        runner.run(new RunNotifier())
        def results = resultsFrom(runner.testOutcomes)
        then:
        def outcome = results["shouldBeAbleToPurchaseAnItemForFree"]
        outcome.result == FAILURE
        outcome.testSteps.collect { it.result } == [FAILURE, SKIPPED, SKIPPED]
    }

}
