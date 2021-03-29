package serenityscreenplay.net.serenitybdd.screenplay.events;

import serenityscreenplay.net.serenitybdd.screenplay.Performable;

public class ActorPerforms extends ActorPerformanceEvent {
    private final Performable performable;

    public ActorPerforms(Performable performable, String actor) {
        super(actor);
        this.performable = performable;
    }

    public Performable getPerformable() {
        return performable;
    }
}
