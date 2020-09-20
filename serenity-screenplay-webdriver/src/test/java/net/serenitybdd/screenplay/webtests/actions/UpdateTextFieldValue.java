package net.serenitybdd.screenplay.webtests.actions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actions.Enter;
import net.serenitybdd.screenplay.actions.Settable;
import net.serenitybdd.screenplay.targets.Target;

import static net.serenitybdd.screenplay.Tasks.instrumented;


public class UpdateTextFieldValue implements Settable {

    private final Target target;
    String newValue;

    public UpdateTextFieldValue(Target target){
        this.target = target;
    }

    @Override
    public UpdateTextFieldValue to(String newValue) {
        this.newValue = newValue;
        return this;
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(Enter.keyValues(newValue).into(target));
    }
}
