package net.serenitybdd.screenplay

import net.serenitybdd.screenplay.conditions.Check
import net.serenitybdd.screenplay.questions.TheMemory
import net.thucydides.core.steps.BaseStepListener
import net.thucydides.core.steps.StepEventBus
import spock.lang.Specification

import java.nio.file.Files

class WhenActorsForgetStuff extends Specification {

    def setup() {
        File temporaryDirectory = Files.createTempDirectory("testdata").toFile();
        def defaultStepListener = new BaseStepListener(temporaryDirectory);
        StepEventBus.getEventBus().registerListener(defaultStepListener);
    }

    def "actor can forget stuff"() {
        given:
        def actor = new Actor("Jill")
        actor.remember("favorite color", "blue")
        when:
        def forgottenColor = actor.forget("favorite color")
        then:
        actor.recall("favorite color") == null
        forgottenColor == "blue"
    }

    def "actor can forget stuff when performing actions"() {
        given:
        def actor = new Actor("Jill")
        actor.remember("favorite color", "blue")
        when:
        actor.attemptsTo(Forget.theValueOf("favorite color"))
        then:
        actor.recall("favorite color") == null
    }

    def "actor can check if he has something in memory"() {
        given:
        def actor = new Actor("Jill")
        when:
        actor.attemptsTo(
                Check.whether(TheMemory.withKey("favorite color").isPresent())
                        .otherwise(
                                RememberThat.theValueOf("favorite color").is("blue")
                        )
        )
        then:
        actor.recall("favorite color") == "blue"
    }
}
