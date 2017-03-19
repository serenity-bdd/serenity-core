package net.serenitybdd.screenplay

import net.serenitybdd.junit.runners.SerenityRunner
import net.serenitybdd.screenplay.shopping.DanaGoesShoppingSample
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
        def outcome = results["shouldBeABleToEvaluateAllTheConsequencesInAGroup"]
        outcome.result == FAILURE
        outcome.testSteps.collect { it.result } == [SUCCESS, SUCCESS, SUCCESS, FAILURE]
        outcome.testSteps.get(3).getDescription() == "Dana should see the correct messages"
    }

    def "should evaluate all the consequences in a group of nested consequences"() {
        given:
        def runner = new SerenityRunner(DanaGoesShoppingSample)
        when:
        runner.run(new RunNotifier())
        def results = resultsFrom(runner.testOutcomes)
        then:
        def outcome = results["shouldBeAbleToEvaluateNestedGroup"]
        outcome.result == FAILURE
        outcome.testSteps.collect { it.result } == [SUCCESS, SUCCESS, SUCCESS, FAILURE]
        outcome.testSteps.get(3).getDescription() == "Then nested thank you message should be 'Thank you!'"
    }

    def "should evaluate all the consequences in a consequence group"() {
        given:
        def runner = new SerenityRunner(DanaGoesShoppingSample)
        when:
        runner.run(new RunNotifier())
        def results = resultsFrom(runner.testOutcomes)
        then:
        def outcome = results["shouldBeAbleToEvaluateConsequenceGroups"]
        outcome.result == SUCCESS
        outcome.testSteps.collect { it.result } == [SUCCESS, SUCCESS, SUCCESS, SUCCESS]
        outcome.testSteps.get(3).getDescription() == "Then the prices should be correctly displayed"
    }


    def "should evaluate all the consequences in a failing consequence group"() {
        given:
        def runner = new SerenityRunner(DanaGoesShoppingSample)
        when:
        runner.run(new RunNotifier())
        def results = resultsFrom(runner.testOutcomes)
        then:
        def outcome = results["shouldBeAbleToEvaluateFailingConsequenceGroups"]
        outcome.result == FAILURE
        outcome.testSteps.collect { it.result } == [SUCCESS, SUCCESS, SUCCESS, FAILURE]
        outcome.testSteps.get(3).getDescription() == "Then the prices should be correctly displayed"
        outcome.testSteps.get(3).getChildren().collect { it.result } == [SUCCESS, FAILURE, SUCCESS]

    }


    def "should evaluate all the consequences in a consequence group with an error"() {
        given:
        def runner = new SerenityRunner(DanaGoesShoppingSample)
        when:
        runner.run(new RunNotifier())
        def results = resultsFrom(runner.testOutcomes)
        then:
        def outcome = results["shouldBeAbleToEvaluateErrorConsequenceGroups"]
        outcome.result == ERROR
        outcome.testSteps.collect { it.result } == [SUCCESS, SUCCESS, SUCCESS, ERROR]
        outcome.testSteps.get(3).getDescription() == "Then the prices should be correctly displayed"
        outcome.testSteps.get(3).getChildren().collect { it.result } == [SUCCESS, ERROR, SUCCESS]

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
