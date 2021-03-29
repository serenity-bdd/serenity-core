package serenityscreenplaywebdriver.net.serenitybdd.screenplay.webtests.tasks;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Performable;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions.Enter;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions.Settable;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.targets.Target;

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
