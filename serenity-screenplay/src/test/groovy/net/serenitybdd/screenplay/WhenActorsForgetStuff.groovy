package net.serenitybdd.screenplay

import net.serenitybdd.screenplay.conditions.Check
import net.serenitybdd.screenplay.questions.TheMemory
import spock.lang.Specification

class WhenActorsForgetStuff extends Specification {

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
