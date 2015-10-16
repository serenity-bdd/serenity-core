package net.serenitybdd.demos.todos.model;

import net.serenitybdd.core.Serenity;
import net.serenitybdd.screenplay.Actor;

public class Actors {
    public static Actor theActorNamed(String name) {

        if (!Serenity.hasASessionVariableCalled(name)) {
            Serenity.setSessionVariable(name).to(Actor.named(name));
        }
        return Serenity.sessionVariableCalled(name);
    }
}
