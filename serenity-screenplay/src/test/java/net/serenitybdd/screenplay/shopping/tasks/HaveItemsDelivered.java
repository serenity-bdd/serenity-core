package net.serenitybdd.screenplay.shopping.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.annotations.Step;

/**
 * Created by john on 9/08/2015.
 */
public class HaveItemsDelivered implements Performable {
    @Override
    @Step("And {0} has them delivered")
    public void performAs(Actor actor) {
    }
}
