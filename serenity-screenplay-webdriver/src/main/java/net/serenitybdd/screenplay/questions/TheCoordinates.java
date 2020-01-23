package net.serenitybdd.screenplay.questions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Coordinates;

import java.util.List;
import java.util.stream.Collectors;

public class TheCoordinates extends TargetedUIState<Coordinates> {

    public TheCoordinates(Target target, Actor actor) {
        super(target,actor);
    }

    public static UIStateReaderBuilder<TheCoordinates> of(Target target) {
        return new UIStateReaderBuilder<>(target, TheCoordinates.class);
    }

    public static UIStateReaderBuilder<TheCoordinates> of(By byLocator) {
        return new UIStateReaderBuilder<>(Target.the(byLocator.toString()).located(byLocator), TheCoordinates.class);
    }

    public static UIStateReaderBuilder<TheCoordinates> of(String locator) {
        return new UIStateReaderBuilder<>(Target.the(locator).locatedBy(locator), TheCoordinates.class);
    }

    public Coordinates resolve() {
        return target.resolveFor(actor).getCoordinates();
    }

    public List<Coordinates> resolveAll() {
        return resolvedElements()
                .map(WebElementFacade::getCoordinates)
                .collect(Collectors.toList());
    }
}
