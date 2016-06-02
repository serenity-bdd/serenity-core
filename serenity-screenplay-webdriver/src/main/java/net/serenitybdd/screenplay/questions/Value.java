package net.serenitybdd.screenplay.questions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.targets.Target;

import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static net.serenitybdd.screenplay.questions.UIFilter.visible;

public class Value extends TargetedUIState<String> {

    public Value(Target target, Actor actor) {
        super(target,actor);
    }

    public static UIStateReaderBuilder<Value> of(Target target) {
        return new UIStateReaderBuilder(target, Value.class);
    }

    public String resolve() {
        return target.resolveFor(actor).getValue();
    }

    public List<String> resolveAll() {
        return extract(visible(target.resolveAllFor(actor)), on(WebElementFacade.class).getValue());
    }
}
