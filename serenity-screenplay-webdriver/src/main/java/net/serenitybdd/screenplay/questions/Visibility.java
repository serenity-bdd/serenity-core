package net.serenitybdd.screenplay.questions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

import java.util.List;
import java.util.stream.Collectors;

public class Visibility extends TargetedUIState<Boolean> {

    public Visibility(Target target, Actor actor) {
        super(target, actor);
    }

    public static UIStateReaderBuilder<Visibility> of(Target target) {
        return new UIStateReaderBuilder(target, Visibility.class);
    }

    public static UIStateReaderBuilder<Visibility> of(By byLocator) {
        return new UIStateReaderBuilder<>(Target.the(byLocator.toString()).located(byLocator), Visibility.class);
    }

    public static UIStateReaderBuilder<Visibility> of(String locator) {
        return new UIStateReaderBuilder<>(Target.the(locator).locatedBy(locator), Visibility.class);
    }

    public Boolean resolve() {
        return target.resolveFor(actor).isVisible();
    }

    public List<Boolean> resolveAll() {
        return target.resolveAllFor(actor).stream()
                .map(WebElementFacade::isVisible)
                .collect(Collectors.toList());
    }
}
