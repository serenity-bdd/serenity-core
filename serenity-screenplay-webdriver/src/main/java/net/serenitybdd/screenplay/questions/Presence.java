package net.serenitybdd.screenplay.questions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

import java.util.List;
import java.util.stream.Collectors;


public class Presence extends TargetedUIState<Boolean> {

    public Presence(Target target, Actor actor) {
        super(target, actor);
    }

    public static UIStateReaderBuilder<Presence> of(Target target) {
        return new UIStateReaderBuilder(target, Presence.class);
    }

    public static UIStateReaderBuilder<Presence> of(By byLocator) {
        return new UIStateReaderBuilder<>(Target.the(byLocator.toString()).located(byLocator), Presence.class);
    }

    public static UIStateReaderBuilder<Presence> of(String locator) {
        return new UIStateReaderBuilder<>(Target.the(locator).locatedBy(locator), Presence.class);
    }

    public Boolean resolve() {
        return target.resolveFor(actor).isPresent();
    }

    public List<Boolean> resolveAll() {
        return target.resolveAllFor(actor).stream()
                .map(WebElementFacade::isPresent)
                .collect(Collectors.toList());
    }
}
