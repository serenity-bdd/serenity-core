package serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.WebElement;

public class HoverOverTarget extends Hover {

    private final Target target;

    public HoverOverTarget(Target target) {
        this.target = target;
    }

    protected WebElement resolveElementFor(Actor actor) {
        return target.resolveFor(actor);
    }

    @Override
    protected String getTarget() {
        return target.toString();
    }
}
