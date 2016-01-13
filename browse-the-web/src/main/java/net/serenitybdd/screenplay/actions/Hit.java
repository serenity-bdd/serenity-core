package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class Hit {

    private Keys[] keys;

    public Hit(Keys[] keys) {
        this.keys = keys;
    }

    public static Hit the(Keys... keys) {
        return new Hit(keys);
    }

    public Performable into(String cssOrXpathForElement) {
        return instrumented(HitTarget.class, keys, Target.the(cssOrXpathForElement).locatedBy(cssOrXpathForElement));
    }

    public Performable into(Target target) {
        return instrumented(HitTarget.class, keys, target);
    }
    public Performable into(WebElementFacade element) {
        return instrumented(HitElement.class, keys, element);
    }
    public Performable into(By... locators) {
        return instrumented(HitBy.class, keys, locators);
    }

    public Performable keyIn(String cssOrXpathForElement) { return into(cssOrXpathForElement); }
    public Performable keyIn(Target target) { return into(target); }
    public Performable keyIn(WebElementFacade element) { return into(element); }

}
