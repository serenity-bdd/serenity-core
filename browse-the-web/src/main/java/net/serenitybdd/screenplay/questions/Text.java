package net.serenitybdd.screenplay.questions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.targets.Target;

import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

public class Text extends TargetedUIState<String> {

    public Text(Target target, Actor actor) {
        super(target, actor);
    }

    public static UIStateReaderBuilder<Text> of(Target target) {
        return new UIStateReaderBuilder(target, Text.class);
    }

    public String resolve() {
        return target.resolveFor(actor).getText();
    }

    public List<String> resolveAll() {
        return extract(target.resolveAllFor(actor), on(WebElementFacade.class).getText());
    }
}
