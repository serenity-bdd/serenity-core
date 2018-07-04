package net.serenitybdd.screenplay.actors;

import net.serenitybdd.screenplay.Ability;
import net.serenitybdd.screenplay.Actor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static java.util.Arrays.asList;

public class SimpleCast extends Cast {

    private final List<Ability> standardAbilities;
    private final List<Consumer<Actor>> abilityProviders;

    public SimpleCast(Ability[] abilities) {
        this.standardAbilities = asList(abilities);
        this.abilityProviders = new ArrayList<>();
    }

    public SimpleCast(Consumer<Actor>... providers) {
        this.standardAbilities = new ArrayList<>();
        this.abilityProviders = asList(providers);
    }

    @Override
    public Actor actorNamed(String actorName, Ability... abilities) {

        Actor newActor = super.actorNamed(actorName, abilities);
        standardAbilities.forEach(
                doSomething -> newActor.whoCan(doSomething)
        );
        abilityProviders.forEach(
                ability -> ability.accept(newActor)
        );
        return newActor;
    }

    public BrowsingActorBuilder actorUsingBrowser(String driver) {
        return new BrowsingActorBuilder(this, driver);
    }

    public class BrowsingActorBuilder {

        private final Cast cast;
        private final String driver;

        public BrowsingActorBuilder(Cast cast, String driver) {
            this.cast = cast;
            this.driver = driver;
        }

        public Actor named(String actorName) {
            return cast.actorNamed(actorName);
        }

    }
}
