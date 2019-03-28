package net.serenitybdd.screenplay

import spock.lang.Specification

class WhenActorsRememberStuff extends Specification {

    def "actor can remember stuff"() {
        given:
            def actor = new Actor("Jill")
        when:
            actor.remember("favorite color","blue")
        then:
            actor.recall("favorite color") == "blue"
    }

    def "actor can remember stuff when performing actions"() {
        given:
            def actor = new Actor("Jill")
        when:
            actor.attemptsTo(RememberThat.theValueOf("favorite color").is("blue"))
        then:
            actor.recall("favorite color") == "blue"
    }

    Question<String> favoriteColor = new Question<String>() {
        @Override
        String answeredBy(Actor actor) {
            return "red"
        }
    }

    def "actor can remember stuff by asking questions when performing actions"() {
        given:
        def actor = new Actor("Jill")
        when:
        actor.attemptsTo(RememberThat.theValueOf("favorite color").isAnsweredBy(favoriteColor))
        then:
        actor.recall("favorite color") == "red"
    }


    def "actor can remember objects"() {
        given:
            def actor = new Actor("Jill")
        when:
            actor.remember("salary",new BigDecimal("100000"))
        then:
            actor.recall("salary") == new BigDecimal("100000")
    }

}
