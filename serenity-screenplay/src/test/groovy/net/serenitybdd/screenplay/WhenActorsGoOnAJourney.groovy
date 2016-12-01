package net.serenitybdd.screenplay
import net.serenitybdd.screenplay.shopping.DanaGoesShoppingSample
import net.serenitybdd.junit.runners.SerenityRunner
import net.serenitybdd.screenplay.shopping.tasks.ATaskWithParameters
import net.thucydides.core.model.TestResult
import org.junit.runner.notification.RunNotifier
import spock.lang.Specification

import static OutcomeChecks.resultsFrom
import static net.serenitybdd.screenplay.Tasks.instrumented
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
                                                                          "Then total cost should be (15)"]
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

    def "should allow performables with different parameter types"() {
        when:
            ATaskWithParameters someTask = instrumented(ATaskWithParameters, 10, "Bill", BigDecimal.ONE )
        then:
            someTask.aPrimitiveType == 10 && someTask.anObject == "Bill" && someTask.aParent == BigDecimal.ONE
    }

    def "should allow performables with null parameter values"() {
        when:
        ATaskWithParameters someTask = instrumented(ATaskWithParameters, 10, null, BigDecimal.ONE )
        then:
        someTask.aPrimitiveType == 10 && someTask.anObject == null && someTask.aParent == BigDecimal.ONE
    }

}
