package net.serenitybdd.screenplay.questions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.targets.Target;

import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

public class CurrentVisibility extends TargetedUIState<Boolean> {

    public CurrentVisibility(Target target, Actor actor) {
        super(target, actor);
    }

    public static UIStateReaderBuilder<CurrentVisibility> of(Target target) {
        return new UIStateReaderBuilder(target, CurrentVisibility.class);
    }

    public Boolean resolve() {
        return target.resolveFor(actor).isCurrentlyVisible();
    }

    public List<Boolean> resolveAll() {
        return extract(target.resolveAllFor(actor), on(WebElementFacade.class).isCurrentlyVisible());
    }
}
