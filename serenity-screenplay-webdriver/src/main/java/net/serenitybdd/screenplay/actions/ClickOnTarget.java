package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.targets.Target;

public class ClickOnTarget extends ClickOnClickable {
    private final Target target;

    @Override
    public WebElementFacade resolveFor(Actor theUser) {
        return target.resolveFor(theUser);
    }

    @Override
    protected String getName() {
        return target.getName();
    }

    public ClickOnTarget(Target target) {
        this.target = target;
    }

}
