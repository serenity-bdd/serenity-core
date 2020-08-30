package net.serenitybdd.screenplay.questions;

import io.vavr.collection.List;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;


public class Value extends TargetedUIState<String> {

    public Value(Target target, Actor actor) {
        super(target, actor);
    }

    public static UIStateReaderBuilder<Value> of(Target target) {
        return new UIStateReaderBuilder(target, Value.class);
    }

    public static UIStateReaderBuilder<Value> of(By byLocator) {
        return new UIStateReaderBuilder<>(Target.the(byLocator.toString()).located(byLocator), Value.class);
    }

    public static UIStateReaderBuilder<Value> of(String locator) {
        return new UIStateReaderBuilder<>(Target.the(locator).locatedBy(locator), Value.class);
    }

    public String resolve() {
        return target.resolveFor(actor).getValue();
    }

    public java.util.List<String> resolveAll() {
        List<WebElementFacade> resolvedElements = List.ofAll(target.resolveAllFor(actor));
        return resolvedElements.map(WebElementFacade::getValue).asJava();
    }
}
