package net.serenitybdd.screenplay.webtests.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.tasks.Enter;
import net.serenitybdd.screenplay.tasks.Settable;

public class UpdateProfileFieldValue implements Settable {

    Target field;
    String newValue;

    public UpdateProfileFieldValue(Target field) {
        this.field = field;
    }

    public <T extends Actor> void performAs(T theUser) {
        theUser.attemptsTo(Enter.theValue(newValue).into(field));
    }

    public Performable to(String newValue) {
        this.newValue = newValue;
        return this;
    }
}
