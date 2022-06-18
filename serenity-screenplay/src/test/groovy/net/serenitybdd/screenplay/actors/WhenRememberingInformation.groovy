package net.serenitybdd.screenplay.actors

import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.Question
import spock.lang.Specification

class WhenRememberingInformation extends Specification {
    def "An actor can remember information"() {
        given:
        Actor actor = Actor.named("Archie")
        when:
        actor.remember("TOTAL_COST", 100)
        then:
        actor.recall("TOTAL_COST") == 100
    }

    Question<Integer> totalCost() {
        return new Question<Integer>() {
            @Override
            Integer answeredBy(Actor actor) {
                return 100
            }
        }
    }

    def "An actor can remember the answer to a question"() {
        given:
        Actor actor = Actor.named("Archie")
        when:
        actor.remember("TOTAL_COST", totalCost())
        then:
        actor.recall("TOTAL_COST") == 100
    }


    def "An actor can forget what they know"() {
        given:
        Actor actor = Actor.named("Archie")
        actor.remember("TOTAL_COST", 10)
        when:
        actor.forget("TOTAL_COST")
        then:
        actor.recall("TOTAL_COST") == null
    }



    def "An actor can recall everything they remember as a hashmap"() {
        given:
        Actor actor = Actor.named("Archie")
        when:
        actor.remember("color", "red")
        actor.remember("number", 7)
        then:
        actor.recallAll() == ["color":"red","number":7]
    }
}
