package net.serenitybdd.screenplay

import spock.lang.Specification

class WhenActorsHaveAbilities extends Specification{

    interface CanLevitate {}

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

    class ReachEnlightenment extends Meditate{
    }

    class AttainNirvana extends Meditate implements CanLevitate {}

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

    def "actors associate extensions of Abilities with the superclass too"() {
        given:
        def actor = Actor.named("Bruce")
        def reachEnlightenment = new ReachEnlightenment()
        when:
        actor.can(reachEnlightenment)
        then:
        actor.abilityTo(Meditate) instanceof ReachEnlightenment

    }

    def "actors don't care what the Ability is called, just what it can do"() {
        given:
        def actor = Actor.named("Bruce")
        def attainNirvana = new AttainNirvana()
        when:
        actor.can(attainNirvana)
        then:
        UseAnAbility.of(actor).that(CanLevitate.class) instanceof CanLevitate

    }

    def "actors won't return just any old Ability"() {
        given:
        def actor = Actor.named("Bruce")
        def reachEnlightenment = new ReachEnlightenment()
        actor.can(reachEnlightenment)
        when:
        UseAnAbility.of(actor).that(CanLevitate.class)
        then:
        thrown NoMatchingAbilityException

    }

}
