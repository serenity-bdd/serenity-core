package net.serenitybdd.screenplay.questions;

import net.serenitybdd.core.pages.WebElementState;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.targets.Target;

import java.util.List;
import java.util.stream.Collectors;

public class Enabled extends TargetedUIState<Boolean> {

    public Enabled(Target target, Actor actor) {
        super(target, actor);
    }

    public static UIStateReaderBuilder<Enabled> of(Target target) {
        return new UIStateReaderBuilder<>(target, Enabled.class);
    }

    public Boolean resolve() {
        return target.resolveFor(actor).isEnabled();
    }

    public List<Boolean> resolveAll() {
        return resolvedElements()
                .map(WebElementState::isEnabled)
                .collect(Collectors.toList());
    }
}
