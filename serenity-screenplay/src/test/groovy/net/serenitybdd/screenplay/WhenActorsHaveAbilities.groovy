package net.serenitybdd.screenplay

import spock.lang.Specification

class WhenActorsHaveAbilities extends Specification{

    class PlayTheGuitar implements Ability {
    }

    class Meditate implements Ability, RefersToActor {
        Actor actor
        @Override
        def <T extends Ability> T asActor(Actor actor) {
            this.actor = actor
            return this;
        }
    }

    def "actors can be associated with Abilities"() {
        given:
        def actor = Actor.named("Bruce")
        when:
        actor.can(new PlayTheGuitar())
        then:
        actor.usingAbilityTo(PlayTheGuitar) != null
    }

    def "actors can be associated with Abilities that are Self Aware"() {
        given:
        def actor = Actor.named("Bruce")
        def meditate = new Meditate()
        when:
        actor.can(meditate)
        then:
        actor.abilityTo(Meditate) != null
        and:
        meditate.actor == actor;

    }

}
