package net.serenitybdd.screenplay.questions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;

import java.util.List;
import java.util.stream.Collectors;

public class TheSize extends TargetedUIState<Dimension> {

    public TheSize(Target target, Actor actor) {
        super(target,actor);
    }

    public static UIStateReaderBuilder<TheSize> of(Target target) {
        return new UIStateReaderBuilder<>(target, TheSize.class);
    }

    public static UIStateReaderBuilder<TheSize> of(By byLocator) {
        return new UIStateReaderBuilder<>(Target.the(byLocator.toString()).located(byLocator), TheSize.class);
    }

    public static UIStateReaderBuilder<TheSize> of(String locator) {
        return new UIStateReaderBuilder<>(Target.the(locator).locatedBy(locator), TheSize.class);
    }

    public Dimension resolve() {
        return target.resolveFor(actor).getSize();
    }

    public List<Dimension> resolveAll() {
        return resolvedElements()
                .map(WebElementFacade::getSize)
                .collect(Collectors.toList());
    }
}
