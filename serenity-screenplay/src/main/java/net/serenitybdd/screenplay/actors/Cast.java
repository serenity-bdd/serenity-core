package net.serenitybdd.screenplay.actors;

import net.serenitybdd.core.collect.NewList;
import java.util.HashMap;
import net.serenitybdd.screenplay.Ability;
import net.serenitybdd.screenplay.Actor;

import java.util.List;
import java.util.Map;

/**
 * Provide simple support for managing Screenplay actors in Cucumber-JVM or JBehave
 */
public class Cast {

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
