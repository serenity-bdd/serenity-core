package net.serenitybdd.screenplay.questions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.targets.Target;

import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

public class SelectedOptions extends TargetedUIState<List<String>> {

    public SelectedOptions(Target target, Actor actor) {
        super(target,actor);
    }

    public static UIStateReaderBuilder<SelectedOptions> of(Target target) {
        return new UIStateReaderBuilder(target, SelectedOptions.class);
    }

    public List<String> resolve() {
        return target.resolveFor(actor).getSelectOptions();
    }

    public List<List<String>> resolveAll() {
        return extract(target.resolveAllFor(actor), on(WebElementFacade.class).getSelectOptions());
    }
}
