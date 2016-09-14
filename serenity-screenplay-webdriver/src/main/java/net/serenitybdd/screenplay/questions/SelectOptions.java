package net.serenitybdd.screenplay.questions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.targets.Target;

import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static net.serenitybdd.screenplay.questions.UIFilter.visible;

public class SelectOptions extends TargetedUIState<List<String>> {

    public SelectOptions(Target target, Actor actor) {
        super(target,actor);
    }

    public static UIStateReaderBuilder<SelectOptions> of(Target target) {
        return new UIStateReaderBuilder(target, SelectOptions.class);
    }

    public List<String> resolve() {
        return target.resolveFor(actor).getSelectOptions();
    }

    public List<List<String>> resolveAll() {
        return extract(visible(target.resolveAllFor(actor)), on(WebElementFacade.class).getSelectOptions());
    }
}
