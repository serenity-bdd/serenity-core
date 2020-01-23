package net.serenitybdd.screenplay.actors

import net.serenitybdd.screenplay.Ability
import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.HasTeardown
import spock.lang.Specification

import java.util.function.Consumer

class WhenRecruitingACast extends Specification {

    def "a cast can provide actors by name"() {
        given:
            Cast cast = new Cast()
        when:
            Actor actor = cast.actorNamed("Joe")
        then:
            actor.name == "Joe"
    }

    static class PerformShakespeare implements Ability {}

    def "cast members can be trained"() {
        given:
            Ability performShakespeare = new PerformShakespeare();
        and:
        Cast globeTheatreCast = Cast.whereEveryoneCan(performShakespeare)
        when:
            Actor laurance = globeTheatreCast.actorNamed("Laurence")
        then:
            laurance.abilityTo(PerformShakespeare.class) == performShakespeare
    }

    static class Fetch implements Ability {
        String item

        Fetch(String item) {
            this.item = item
        }

        static some(String item) {
            return new Fetch(item)
        }
    }

    def "cast members can be trained to do arbitrary things"() {
        given:
            Consumer<Actor> fetchTheCoffee = {actor -> actor.whoCan(Fetch.some("Coffee"))}
            Cast globeTheatreCast = Cast.whereEveryoneCan(fetchTheCoffee)
        when:
            Actor kenneth = globeTheatreCast.actorNamed("Kenneth")
        then:
            kenneth.abilityTo(Fetch.class).item == "Coffee"
    }

    static class PerformHamlet implements Ability, HasTeardown {

        @Override
        void tearDown() {}
    }

    def "cast members can tidy up after themselves"() {
        given:
            PerformHamlet performHamlet = Mock(PerformHamlet.class)
            OnStage.setTheStage(Cast.whereEveryoneCan(performHamlet))
            OnStage.theActorCalled("Laurence")
        when:
            OnStage.drawTheCurtain()
        then:
            1 * performHamlet.tearDown()
    }

    def "cast members can be used before knowing their names"() {
        given:
            OnStage.setTheStage(Cast.ofStandardActors())
            Actor theNextActor = OnStage.aNewActor()
        when:
            Actor kenneth = OnStage.theActorCalled("Kenneth");
        then:
            theNextActor == kenneth;
    }

}
