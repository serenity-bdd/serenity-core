package net.serenitybdd.screenplay.actors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import net.serenitybdd.screenplay.Ability;
import net.serenitybdd.screenplay.Actor;

import java.util.List;
import java.util.Map;

/**
 * Provide simple support for managing Screenplay actors in Cucumber-JVM or JBehave
 */
public class Cast {

    Map<String, Actor> actors = Maps.newHashMap();

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
        return ImmutableList.copyOf(actors.values());
    }

    public void dismissAll() {
        actors.clear();
    }

}
