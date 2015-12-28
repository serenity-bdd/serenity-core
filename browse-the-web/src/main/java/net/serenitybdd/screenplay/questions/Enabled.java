package net.serenitybdd.screenplay.questions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.targets.Target;

import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

public class Enabled extends UIState<Boolean> {

    public Enabled(Target target, Actor actor) {
        super(target, actor);
    }

    public static UIStateReaderBuilder<Enabled> of(Target target) {
        return new UIStateReaderBuilder(target, Enabled.class);
    }

    public Boolean resolve() {
        return target.resolveFor(actor).isEnabled();
    }

    public List<Boolean> resolveAll() {
        return extract(target.resolveAllFor(actor), on(WebElementFacade.class).isEnabled());
    }
}
