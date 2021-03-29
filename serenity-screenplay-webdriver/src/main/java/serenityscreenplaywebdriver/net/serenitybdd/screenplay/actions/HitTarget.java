package serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Interaction;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.targets.Target;
import serenitymodel.net.thucydides.core.annotations.Step;
import org.openqa.selenium.Keys;

import java.util.Arrays;

public class HitTarget implements Interaction {

    private Keys[] keys;
    private Target target;

    public HitTarget(Keys[] keys, Target target) {
        this.keys = Arrays.copyOf(keys,keys.length);
        this.target = target;
    }

    @Step("{0} types '#keys' in #target")
    public <T extends Actor> void performAs(T theUser) {
        target.resolveFor(theUser).sendKeys(keys);
    }
}
