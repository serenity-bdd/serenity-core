package net.serenitybdd.screenplay.questions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

import java.util.List;
import java.util.stream.Collectors;

public class SelectedVisibleTextValue extends TargetedUIState<String> {

    public SelectedVisibleTextValue(Target target, Actor actor) {
        super(target,actor);
    }

    public static UIStateReaderBuilder<SelectedVisibleTextValue> of(Target target) {
        return new UIStateReaderBuilder(target, SelectedVisibleTextValue.class);
    }

    public static UIStateReaderBuilder<SelectedVisibleTextValue> of(By byLocator) {
        return new UIStateReaderBuilder<>(Target.the(byLocator.toString()).located(byLocator), SelectedVisibleTextValue.class);
    }

    public static UIStateReaderBuilder<SelectedVisibleTextValue> of(String locator) {
        return new UIStateReaderBuilder<>(Target.the(locator).locatedBy(locator), SelectedVisibleTextValue.class);
    }

    public String resolve() {
        return target.resolveFor(actor).getSelectedVisibleTextValue();
    }

    public List<String> resolveAll() {
        return target.resolveAllFor(actor).stream()
                .map(WebElementFacade::getSelectedVisibleTextValue)
                .collect(Collectors.toList());
    }
}
