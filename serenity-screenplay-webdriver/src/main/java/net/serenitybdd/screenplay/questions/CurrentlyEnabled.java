package net.serenitybdd.screenplay.questions;

import net.serenitybdd.core.pages.WebElementState;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.targets.Target;

import java.util.List;
import java.util.stream.Collectors;

public class CurrentlyEnabled extends TargetedUIState<Boolean> {

    public CurrentlyEnabled(Target target, Actor actor) {
        super(target, actor);
    }

    public static UIStateReaderBuilder<CurrentlyEnabled> of(Target target) {
        return new UIStateReaderBuilder<>(target, CurrentlyEnabled.class);
    }

    public Boolean resolve() {
        return target.resolveFor(actor).isCurrentlyEnabled();
    }

    public List<Boolean> resolveAll() {
        return resolvedElements()
                .map(WebElementState::isCurrentlyEnabled)
                .collect(Collectors.toList());
    }
}
