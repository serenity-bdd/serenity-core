package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.core.targets.Target;
import net.serenitybdd.screenplay.Performable;
import org.openqa.selenium.Keys;

public class Hit {

    private Keys[] keys;

    public Hit(Keys[] keys) {
        this.keys = keys;
    }

    public static Hit the(Keys... keys) {
        return new Hit(keys);
    }

    public Performable into(String cssOrXpathForElement) {
        return new HitTarget(keys, Target.the(cssOrXpathForElement).locatedBy(cssOrXpathForElement));
    }

    public Performable into(Target target) {
        return new HitTarget(keys, target);
    }

    public Performable into(WebElementFacade element) {
        return new HitElement(keys, element);
    }
    public Performable keyIn(String cssOrXpathForElement) { return into(cssOrXpathForElement); }
    public Performable keyIn(Target target) { return into(target); }
    public Performable keyIn(WebElementFacade element) { return into(element); }

}
