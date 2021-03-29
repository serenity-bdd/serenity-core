package serenityscreenplay.net.serenitybdd.screenplay.events;

public abstract class ActorPerformanceEvent {
    private final String name;

    public ActorPerformanceEvent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
