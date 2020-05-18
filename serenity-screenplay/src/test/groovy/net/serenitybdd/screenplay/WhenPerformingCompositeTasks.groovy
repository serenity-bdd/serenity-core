package net.serenitybdd.screenplay

import net.serenitybdd.screenplay.conditions.Check
import net.serenitybdd.screenplay.shopping.BitesTheBanana
import net.serenitybdd.screenplay.shopping.ChewsTheBanana
import net.serenitybdd.screenplay.shopping.PeelABanana
import net.thucydides.core.model.TestResult
import net.thucydides.core.steps.BaseStepListener
import net.thucydides.core.steps.StepEventBus
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification


class WhenPerformingCompositeTasks extends Specification {

    @Rule
    TemporaryFolder temporaryFolder
    File temporaryDirectory
    BaseStepListener listener = new BaseStepListener(temporaryDirectory)

    def setup() {
        temporaryDirectory = temporaryFolder.newFolder()
        StepEventBus.eventBus.clear()
        StepEventBus.eventBus.registerListener(listener)
        StepEventBus.eventBus.testStarted("some test")
    }

    def "A composite task should execute all of the nested tasks"() {

        given:
        Performable eatsABanana = new PeelABanana().then(new BitesTheBanana()).then(new ChewsTheBanana())
        when:
        Actor.named("Eddie").attemptsTo(eatsABanana);
        then:
        testPassed()
        and:
        testOutcomeContainsStep("Eddie peels a banana")
        testOutcomeContainsStep("Eddie bites the banana")
        testOutcomeContainsStep("Eddie chews the banana")
    }


    def testPassed() {
        listener.latestTestOutcome().get().result == TestResult.SUCCESS
    }

    def testOutcomeContainsStep(String expectedDescription) {
        listener.latestTestOutcome().get().testSteps.find {
            step ->
                step.description == expectedDescription}
    }

    def testOutcomeContainsNoSteps() {
        listener.latestTestOutcome().get().testSteps.isEmpty()
    }
}