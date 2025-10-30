package net.serenitybdd.screenplay

import net.serenitybdd.core.di.SerenityInfrastructure
import net.thucydides.core.steps.BaseStepListener
import net.thucydides.core.steps.StepEventBus
import net.thucydides.model.domain.TestResult
import net.thucydides.model.environment.TestLocalEnvironmentVariables
import net.thucydides.model.util.EnvironmentVariables
import spock.lang.Specification

import java.nio.file.Files

import static net.thucydides.model.ThucydidesSystemProperty.MANUAL_TASK_INSTRUMENTATION

class WhenInstrumentingTasks extends Specification {

    File temporaryDirectory
    BaseStepListener listener = new BaseStepListener(temporaryDirectory)
    EnvironmentVariables environmentVariables
    boolean currentManualInstrumentationSetting

    def setup() {
        environmentVariables = SerenityInfrastructure.environmentVariables
        currentManualInstrumentationSetting = environmentVariables.getPropertyAsBoolean(MANUAL_TASK_INSTRUMENTATION, false)
        temporaryDirectory = Files.createTempDirectory("tmp").toFile()
        temporaryDirectory.deleteOnExit()

        StepEventBus.eventBus.clear()
        StepEventBus.eventBus.registerListener(listener)
        StepEventBus.eventBus.testStarted("some test")
    }

    def cleanup() {
        environmentVariables.setProperty(MANUAL_TASK_INSTRUMENTATION.toString(), currentManualInstrumentationSetting.toString())
    }

    def "A non-instrumented class will be reported by default"() {

        given:
            Performable basicTask = new EatsAnApple()
        when:
            Actor.named("eddie").attemptsTo(basicTask)
        then:
            testPassed()
        and:
            testOutcomeContainsStep("Eddie eats an apple")
    }


    def "A non-instrumented class with a builder will not be reported as long as it has a constructor"() {

        given:
        Performable basicTask = EatsFruit.loudly()
        when:
            Actor.named("Eddie").attemptsTo(basicTask)
        then:
            testPassed()
        and:
            testOutcomeContainsStep("Eddie eats a peach")
    }

    def "A non-instrumented class will not be reported if it doesn't have a default constructor"() {

        given:
            Performable basicTask = EatsARockmelon.quietly()
        when:
            Actor.named("eddie").attemptsTo(basicTask)
        then:
            testPassed()
        and:
            testOutcomeContainsNoSteps()
    }


    def "The @Step annotation can be used to customise the step label"() {

        given:
            Performable basicTask = new EatsAnOrange()
        when:
            Actor.named("Annie").attemptsTo(basicTask)
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

    def "Tasks with the IsHidden marker interface will not be reported"() {

        given:
            Performable basicTask = new EatsAMango()
        when:
            Actor.named("Annie").attemptsTo(basicTask)
        then:
            testPassed()
        and:
            testOutcomeContainsNoSteps()
    }

    def "Nested tasks of a task having IsHidden marker interface will be reported"() {

        given:
            Performable nestedTask = new EatsAnApple()
            Performable wrapperTask = new Eats(nestedTask)
        when:
            Actor.named("Annie").attemptsTo(wrapperTask)
        then:
            testPassed()
        and:
            testOutcomeDoesNotContainStep("Annie eats the given fruit")
            testOutcomeContainsStep("Annie eats an apple")
    }

    def "Tasks with the IsSilent marker interface will not be reported"() {

        given:
            Performable basicTask = new EatsATangarine()
        when:
            Actor.named("Annie").attemptsTo(basicTask)
        then:
            testPassed()
        and:
            testOutcomeContainsNoSteps()
    }


    def "Instrumented tasks with the CanBeSilent marker interface can be configured to be reported or not"() {

        when:
            Actor.named("Annie").attemptsTo(EatsAWatermelon.quietly(), EatsAWatermelon.noisily())
        then:
            testPassed()
        and:
            listener.latestTestOutcome().get().testSteps.size() == 1
            testOutcomeContainsStep("Annie eats a watermelon loudly")
    }


    def "The @Step annotation is ignored in test output if task is manually instantiated and MANUAL_TASK_INSTRUMENTATION is true"() {

        given:
        TestLocalEnvironmentVariables.setProperty(MANUAL_TASK_INSTRUMENTATION.toString(), "true")
        Performable basicTask = new EatsAnOrange()
        when:
        Actor.named("Annie").attemptsTo(basicTask)
        then:
        testPassed()
        and:
        testOutcomeContainsNoSteps()
    }

    def "There should be no test output if a task is manually instantiated and MANUAL_TASK_INSTRUMENTATION is true"() {

        given:
        TestLocalEnvironmentVariables.setProperty(MANUAL_TASK_INSTRUMENTATION.toString(), "true")
        Performable basicTask = EatsFruit.loudly()
        when:
        Actor.named("Eddie").attemptsTo(basicTask)
        then:
        testPassed()
        and:
        testOutcomeContainsNoSteps()
    }


    def testPassed() {
        listener.latestTestOutcome().get().result == TestResult.SUCCESS
    }

    def testOutcomeContainsStep(String expectedDescription) {
        listener.latestTestOutcome().get().testSteps.find { step -> step.description == expectedDescription}
    }

    def testOutcomeDoesNotContainStep(String expectedDescription) {
        listener.latestTestOutcome().get().testSteps.every { step -> step.description != expectedDescription}
    }

    def testOutcomeContainsNoSteps() {
        listener.latestTestOutcome().get().testSteps.isEmpty()
    }
}
