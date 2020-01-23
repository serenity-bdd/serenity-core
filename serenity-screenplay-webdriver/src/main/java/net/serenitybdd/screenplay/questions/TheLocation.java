package net.serenitybdd.screenplay.questions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;

import java.util.List;
import java.util.stream.Collectors;

public class TheLocation extends TargetedUIState<Point> {

    public TheLocation(Target target, Actor actor) {
        super(target,actor);
    }

    public static UIStateReaderBuilder<TheLocation> of(Target target) {
        return new UIStateReaderBuilder<>(target, TheLocation.class);
    }

    public static UIStateReaderBuilder<TheLocation> of(By byLocator) {
        return new UIStateReaderBuilder<>(Target.the(byLocator.toString()).located(byLocator), TheLocation.class);
    }

    public static UIStateReaderBuilder<TheLocation> of(String locator) {
        return new UIStateReaderBuilder<>(Target.the(locator).locatedBy(locator), TheLocation.class);
    }

    public Point resolve() {
        return target.resolveFor(actor).getLocation();
    }

    public List<Point> resolveAll() {
        return resolvedElements()
                .map(WebElementFacade::getLocation)
                .collect(Collectors.toList());
    }
}
