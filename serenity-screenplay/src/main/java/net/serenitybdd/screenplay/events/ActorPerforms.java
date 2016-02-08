package net.serenitybdd.screenplay.events;

import net.serenitybdd.screenplay.Performable;

public class ActorPerforms {
    private final Performable performable;

    public ActorPerforms(Performable performable) {
        this.performable = performable;
    }

    public Performable getPerformable() {
        return performable;
    }
}
