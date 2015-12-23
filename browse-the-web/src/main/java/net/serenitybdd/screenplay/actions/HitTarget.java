package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.targets.Target;
import net.serenitybdd.screenplay.Action;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.thucydides.core.annotations.Step;
import org.openqa.selenium.Keys;

public class HitTarget implements Action {

    private Keys[] keys;
    private Target target;

    public HitTarget(Keys[] keys, Target target) {
        this.keys = keys;
        this.target = target;
    }

    @Step("{0} types '#keys' in #target")
    public <T extends Actor> void performAs(T theUser) {
        BrowseTheWeb.as(theUser).findBy(target).then().sendKeys(keys);
    }
}
