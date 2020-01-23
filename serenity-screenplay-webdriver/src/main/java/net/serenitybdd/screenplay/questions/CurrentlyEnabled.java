package net.serenitybdd.screenplay.questions;

import net.serenitybdd.core.pages.WebElementState;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

import java.util.List;
import java.util.stream.Collectors;

public class CurrentlyEnabled extends TargetedUIState<Boolean> {

    public CurrentlyEnabled(Target target, Actor actor) {
        super(target, actor);
    }

    public static UIStateReaderBuilder<CurrentlyEnabled> of(Target target) {
        return new UIStateReaderBuilder<>(target, CurrentlyEnabled.class);
    }

    public static UIStateReaderBuilder<CurrentlyEnabled> of(By byLocator) {
        return new UIStateReaderBuilder<>(Target.the(byLocator.toString()).located(byLocator), CurrentlyEnabled.class);
    }

    public static UIStateReaderBuilder<CurrentlyEnabled> of(String locator) {
        return new UIStateReaderBuilder<>(Target.the(locator).locatedBy(locator), CurrentlyEnabled.class);
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
