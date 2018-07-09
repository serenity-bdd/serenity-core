package net.serenitybdd.screenplay.facts;

import net.serenitybdd.screenplay.Actor;

/**
 * A Fact is a way of declaring details about an actor.
 * This can be used to setup or teardown test data for the actor.
 */
public interface Fact {

    void setup(Actor actor);

    default void teardown(Actor actor) {}
}