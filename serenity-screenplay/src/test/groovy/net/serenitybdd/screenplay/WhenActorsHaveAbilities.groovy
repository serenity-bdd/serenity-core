package net.serenitybdd.screenplay

import spock.lang.Specification

class WhenActorsHaveAbilities extends Specification{

    class PlayScrabble implements Ability {
        @Override
        def <T extends Ability> T asActor(Actor actor) {
            return this;
        }
    }

    def "an actor can have the ability to do stuff"() {

    }
}
