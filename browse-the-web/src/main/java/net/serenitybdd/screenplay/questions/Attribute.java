package net.serenitybdd.screenplay.questions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.targets.Target;

import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

public class Attribute extends TargetedUIState<String> {

    private final String attributeName;

    public Attribute(Target target, Actor actor, String attributeName) {
        super(target,actor);
        this.attributeName = attributeName;
    }

    public static UIStateReaderWithNameBuilder<Attribute> of(Target target) {
        return new UIStateReaderWithNameBuilder(target, Attribute.class);
    }

    public String resolve() {
        return target.resolveFor(actor).getAttribute(attributeName);
    }

    public List<String> resolveAll() {
        return extract(target.resolveAllFor(actor), on(WebElementFacade.class).getAttribute(attributeName));
    }
}
