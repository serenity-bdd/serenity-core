package net.serenitybdd.screenplay


import net.thucydides.core.model.TestResult
import net.thucydides.core.steps.BaseStepListener
import net.thucydides.core.steps.StepEventBus
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class WhenInstrumentingTasks extends Specification {

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

    def "A non-instrumented class will be reported by default"() {

        given:
            Performable basicTask = new EatsAnApple()
        when:
            Actor.named("eddie").attemptsTo(basicTask);
        then:
            testPassed()
        and:
            testOutcomeContainsStep("Eddie eats an apple")
    }


    def "A non-instrumented class with a builder will not be reported as long as it has a constructor"() {

        given:
            Performable basicTask = EatsAPeach.loudly()
        when:
            Actor.named("Eddie").attemptsTo(basicTask);
        then:
            testPassed()
        and:
            testOutcomeContainsStep("Eddie eats a peach")
    }

    def "A non-instrumented class will not be reported if it doesn't have a default constructor"() {

        given:
            Performable basicTask = EatsARockmelon.quietly()
        when:
            Actor.named("eddie").attemptsTo(basicTask);
        then:
            testPassed()
        and:
            testOutcomeContainsNoSteps()
    }


    def "The @Step annotation can be used to customise the step label"() {

        given:
            Performable basicTask = new EatsAnOrange()
        when:
            Actor.named("Annie").attemptsTo(basicTask);
        then:
            testPassed()
        and:
            testOutcomeContainsStep("Annie eats a large orange")
    }


    def "For classes without a default constructor, you use an explicitly instrumented class"() {

        when:
            Actor.named("Annie").attemptsTo(EatsAPear.ofSize("large"))
        then:
            testPassed()
        and:
            testOutcomeContainsStep("Annie eats a large pear")
    }

    def "Tasks with the IsSilent marker interface will not be reported"() {

        given:
            Performable basicTask = new EatsATangarine()
        when:
            Actor.named("Annie").attemptsTo(basicTask);
        then:
            testPassed()
        and:
            testOutcomeContainsNoSteps()
    }


    def "Instrumented tasks with the CanBeSilent marker interface can be configured to be reported or not"() {

        when:
            Actor.named("Annie").attemptsTo(EatsAWatermelon.quietly(), EatsAWatermelon.noisily());
        then:
            testPassed()
        and:
            listener.latestTestOutcome().get().testSteps.size() == 1
            testOutcomeContainsStep("Annie eats a watermelon loudly")
    }

    def testPassed() {
        listener.latestTestOutcome().get().result == TestResult.SUCCESS
    }

    def testOutcomeContainsStep(String expectedDescription) {
        listener.latestTestOutcome().get().testSteps.find { step -> step.description == expectedDescription}
    }

    def testOutcomeContainsNoSteps() {
        listener.latestTestOutcome().get().testSteps.isEmpty()
    }
}
