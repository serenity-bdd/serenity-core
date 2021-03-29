package serenityscreenplaywebdriver.net.serenitybdd.screenplay.webtests.tasks;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Performable;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions.SelectFromOptions;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions.Settable;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.targets.Target;

/**
 * Created by john on 22/08/2015.
 */
public class UpdateProfileOption implements Settable {

    Target field;
    String newValue;

    public UpdateProfileOption(Target field) {
        this.field = field;
    }

    public <T extends Actor> void performAs(T theUser) {
        theUser.attemptsTo(SelectFromOptions.byVisibleText(newValue).from(field));
    }

    public Performable to(String newValue) {
        this.newValue = newValue;
        return this;
    }
}
