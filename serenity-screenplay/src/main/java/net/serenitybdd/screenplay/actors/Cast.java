package net.serenitybdd.screenplay.actors;

import net.serenitybdd.model.collect.NewList;

import java.util.ArrayList;
import java.util.HashMap;
import net.serenitybdd.screenplay.Ability;
import net.serenitybdd.screenplay.Actor;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static java.util.Arrays.asList;

/**
 * Provide simple support for managing Screenplay actors in Cucumber-JVM or JBehave
 */
public class Cast {

    private final List<Ability> standardAbilities;
    private final List<Consumer<Actor>> abilityProviders;

    public Cast(Ability[] abilities) {
        this.standardAbilities = asList(abilities);
        this.abilityProviders = new ArrayList<>();
    }

    public Cast(Consumer<Actor>... providers) {
        this.standardAbilities = new ArrayList<>();
        this.abilityProviders = asList(providers);
    }

    public static Cast ofStandardActors() {
        return new Cast();
    }

    /**
     * Create a Cast object with a list of predefined abilities
     */
    public static Cast whereEveryoneCan(Ability... abilities) {
        return new Cast(abilities);
    }

    /**
     * Create a Cast object where each actor is configured using the provided function.
     * E.g.
     *     Cast globeTheatreCast = Cast.whereEveryoneCan(actor -> actor.whoCan(Fetch.some("Coffee")));
     */
    public static Cast whereEveryoneCan(Consumer<Actor>... abilities) {
        return new Cast(abilities);
    }

    Map<String, Actor> actors = new HashMap();

    public Actor actorNamed(String actorName, Ability... abilities) {

        if (! actors.containsKey(actorName)) {
            Actor newActor = Actor.named(actorName);

            for(Ability doSomething : abilities) {
                newActor.can(doSomething);
            }

            assignGeneralAbilitiesTo(newActor);

            actors.put(actorName, newActor);
        }
        return actors.get(actorName);
    }

    public List<Actor> getActors() {
        return NewList.copyOf(actors.values());
    }

    public void dismissAll() {
        for (Actor actor : actors.values()) {
            actor.wrapUp();
        }
        actors.clear();
    }

    protected void assignGeneralAbilitiesTo(Actor newActor) {
        standardAbilities.forEach(newActor::whoCan);
        abilityProviders.forEach(
                ability -> ability.accept(newActor)
        );
    }

}
