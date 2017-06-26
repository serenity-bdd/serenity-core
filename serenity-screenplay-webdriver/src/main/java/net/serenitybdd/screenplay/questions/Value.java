package net.serenitybdd.screenplay.questions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.targets.Target;

import io.vavr.collection.List;


public class Value extends TargetedUIState<String> {

    public Value(Target target, Actor actor) {
        super(target, actor);
    }

    public static UIStateReaderBuilder<Value> of(Target target) {
        return new UIStateReaderBuilder(target, Value.class);
    }

    public String resolve() {
        return target.resolveFor(actor).getValue();
    }

    public java.util.List<String> resolveAll() {
        List<WebElementFacade> resolvedElements = List.ofAll(target.resolveAllFor(actor));
        return resolvedElements.map(WebElementFacade::getValue).asJava();
    }
}
