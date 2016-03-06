package net.serenitybdd.screenplay;

/**
 * A marker interface to indicate that an actor can perform some ability.
 */
public interface Ability {
    <T extends Ability> T asActor(Actor actor);
}
