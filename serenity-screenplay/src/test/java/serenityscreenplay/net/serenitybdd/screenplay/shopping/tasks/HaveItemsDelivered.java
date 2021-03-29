package serenityscreenplay.net.serenitybdd.screenplay.shopping.tasks;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Performable;
import serenitymodel.net.thucydides.core.annotations.Step;

/**
 * Created by john on 9/08/2015.
 */
public class HaveItemsDelivered implements Performable {
    @Override
    @Step("And {0} has them delivered")
    public void performAs(Actor actor) {
    }
}
