package net.serenitybdd.screenplay.questions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

import java.util.List;
import java.util.stream.Collectors;

public class SelectOptions extends TargetedUIState<List<String>> {

    public SelectOptions(Target target, Actor actor) {
        super(target,actor);
    }

    public static UIStateReaderBuilder<SelectOptions> of(Target target) {
        return new UIStateReaderBuilder<>(target, SelectOptions.class);
    }

    public static UIStateReaderBuilder<SelectOptions> of(By byLocator) {
        return new UIStateReaderBuilder<>(Target.the(byLocator.toString()).located(byLocator), SelectOptions.class);
    }

    public static UIStateReaderBuilder<SelectOptions> of(String locator) {
        return new UIStateReaderBuilder<>(Target.the(locator).locatedBy(locator), SelectOptions.class);
    }

    public List<String> resolve() {
        return target.resolveFor(actor).getSelectOptions();
    }

    public List<List<String>> resolveAll() {
        return resolvedElements()
                .map(WebElementFacade::getSelectOptions)
                .collect(Collectors.toList());
    }
}
