package net.serenitybdd.screenplay.questions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

import java.util.List;
import java.util.stream.Collectors;

public class CurrentVisibility extends TargetedUIState<Boolean> {

    public CurrentVisibility(Target target, Actor actor) {
        super(target, actor);
    }

    public static UIStateReaderBuilder<CurrentVisibility> of(Target target) {
        return new UIStateReaderBuilder(target, CurrentVisibility.class);
    }

    public static UIStateReaderBuilder<CurrentVisibility> of(By byLocator) {
        return new UIStateReaderBuilder<>(Target.the(byLocator.toString()).located(byLocator), CurrentVisibility.class);
    }

    public static UIStateReaderBuilder<CurrentVisibility> of(String locator) {
        return new UIStateReaderBuilder<>(Target.the(locator).locatedBy(locator), CurrentVisibility.class);
    }

    public Boolean resolve() {
        return target.resolveFor(actor).isCurrentlyVisible();
    }

    public List<Boolean> resolveAll() {
        return target.resolveAllFor(actor).stream()
                .map(WebElementFacade::isCurrentlyVisible)
                .collect(Collectors.toList());
    }
}
