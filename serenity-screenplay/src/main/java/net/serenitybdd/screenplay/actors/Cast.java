package net.serenitybdd.screenplay.actors;

import net.serenitybdd.core.collect.NewList;
import java.util.HashMap;
import net.serenitybdd.screenplay.Ability;
import net.serenitybdd.screenplay.Actor;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Provide simple support for managing Screenplay actors in Cucumber-JVM or JBehave
 */
public class Cast {

    public static Cast ofStandardUsers() {
        return new SimpleCast();
    }

    /**
     * Create a Cast object with a list of predefined abilities
     */
    public static Cast whereEveryoneCan(Ability... abilities) {
        return new SimpleCast(abilities);
    }

    /**
     * Create a Cast object where each actor is configured using the provided function.
     * E.g.
     *     Cast globeTheatreCast = Cast.whereEveryoneCan(actor -> actor.whoCan(Fetch.some("Coffee")));
     */
    public static Cast whereEveryoneCan(Consumer<Actor>... abilities) {
        return new SimpleCast(abilities);
    }

    Map<String, Actor> actors = new HashMap();

    public Actor actorNamed(String actorName, Ability... abilities) {

        if (! actors.containsKey(actorName)) {
            Actor newActor = Actor.named(actorName);

            for(Ability doSomething : abilities) {
                newActor.can(doSomething);
            }
            actors.put(actorName, newActor);
        }
        return actors.get(actorName);
    }

    public List<Actor> getActors() {
        return NewList.copyOf(actors.values());
    }

    public void dismissAll() {
        actors.clear();
    }

}
