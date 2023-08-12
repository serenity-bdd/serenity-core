package net.serenitybdd.screenplay;

import net.serenitybdd.markers.IsHidden;
import net.serenitybdd.annotations.Step;

public class Eats implements Performable, IsHidden {

    private Performable nestedTask;

    public Eats() { }

    public Eats(Performable nestedTask) {
        this.nestedTask = nestedTask;
    }

    @Step("{0} eats the given fruit")
    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(nestedTask);
    }

}
