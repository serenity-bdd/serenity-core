package net.serenitybdd.screenplay.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

import java.util.List;
import java.util.stream.Collectors;

public class CSSValue extends TargetedUIState<String> {

    private final String attributeName;

    public CSSValue(Target target, Actor actor, String attributeName) {
        super(target,actor);
        this.attributeName = attributeName;
    }

    public static UIStateReaderWithNameBuilder<CSSValue> of(Target target) {
        return new UIStateReaderWithNameBuilder<>(target, CSSValue.class);
    }

    public static UIStateReaderBuilder<CSSValue> of(By byLocator) {
        return new UIStateReaderBuilder<>(Target.the(byLocator.toString()).located(byLocator), CSSValue.class);
    }

    public static UIStateReaderBuilder<CSSValue> of(String locator) {
        return new UIStateReaderBuilder<>(Target.the(locator).locatedBy(locator), CSSValue.class);
    }

    public String resolve() {
        return target.resolveFor(actor).getCssValue(attributeName);
    }

    public List<String> resolveAll() {
        return resolvedElements()
                .map(element -> element.getCssValue(attributeName))
                .collect(Collectors.toList());
    }
}
