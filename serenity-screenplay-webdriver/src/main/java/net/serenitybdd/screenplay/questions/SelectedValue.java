package net.serenitybdd.screenplay.questions;

import net.serenitybdd.core.pages.WebElementState;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.targets.Target;

import java.util.List;
import java.util.stream.Collectors;

public class SelectedValue extends TargetedUIState<String> {

    public SelectedValue(Target target, Actor actor) {
        super(target,actor);
    }

    public static UIStateReaderBuilder<SelectedValue> of(Target target) {
        return new UIStateReaderBuilder<>(target, SelectedValue.class);
    }

    public String resolve() {
        return target.resolveFor(actor).getSelectedValue();
    }

    public List<String> resolveAll() {
        return resolvedElements()
                .map(WebElementState::getSelectedValue)
                .collect(Collectors.toList());
    }
}
