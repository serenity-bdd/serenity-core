package net.serenitybdd.screenplay.questions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.targets.Target;

import java.util.List;
import java.util.stream.Collectors;

public class TheCoordinates extends TargetedUIState<org.openqa.selenium.interactions.internal.Coordinates> {

    public TheCoordinates(Target target, Actor actor) {
        super(target,actor);
    }

    public static UIStateReaderBuilder<TheCoordinates> of(Target target) {
        return new UIStateReaderBuilder<>(target, TheCoordinates.class);
    }

    public org.openqa.selenium.interactions.internal.Coordinates resolve() {
        return target.resolveFor(actor).getCoordinates();
    }

    public List<org.openqa.selenium.interactions.internal.Coordinates> resolveAll() {
        return resolvedElements()
                .map(WebElementFacade::getCoordinates)
                .collect(Collectors.toList());
    }
}
