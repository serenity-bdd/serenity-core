package net.serenitybdd.screenplay;

import net.serenitybdd.markers.IsSilent;

public class Forget implements Interaction, IsSilent {
    private final String memoryKey;

    public Forget(String memoryKey) {
        this.memoryKey = memoryKey;
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.forget(memoryKey);
    }

    public static Forget theValueOf(String memoryKey) {
        return new Forget(memoryKey);
    }

}
