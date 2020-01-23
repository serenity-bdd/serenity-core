package net.serenitybdd.screenplay.actions;

import net.serenitybdd.screenplay.Actor;
import org.openqa.selenium.WebElement;

public class HoverOverElement extends Hover {

    private final WebElement target;

    public HoverOverElement(WebElement target) {
        this.target = target;
    }

    protected WebElement resolveElementFor(Actor actor) {
        return target;
    }

    @Override
    protected String getTarget() {
        return target.toString();
    }
}
