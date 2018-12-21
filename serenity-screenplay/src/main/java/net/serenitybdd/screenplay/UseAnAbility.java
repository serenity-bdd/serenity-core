package net.serenitybdd.screenplay;

/**
 * A factory to get an {@link Ability} of an {@link Actor}, when we don't care what it is specifically, just
 * that it can do what we expect it to.
 * Example Usage:
 * {@code UseAnAbility.of(actor).that(CanLevitate.class).hover()}
 */
public class UseAnAbility {

    private Actor actor;

    private UseAnAbility(Actor actor) {
        this.actor = actor;
    }

    public static UseAnAbility of(Actor actor) {
        return new UseAnAbility(actor);
    }

    /**
     * If the {@link #actor} has an {@link Ability} that implements the Interface, return that. If
     * there are multiple candidate Abilities, the first one found will be returned.
     *
     * @param implementedInterface the Interface class that we expect to find
     * @param <C>                  the implementation of the Interface
     * @return the Ability that implements the interface
     */
    public <C> C that(Class<C> implementedInterface) {
        C ability = this.actor.getAbilityThatExtends(implementedInterface);

        if (ability == null) {
            throw new NoMatchingAbilityException(
                    String.format("%s does not have an Ability that extends %s", actor, implementedInterface.getName())
            );
        }
        return ability;
    }
}
