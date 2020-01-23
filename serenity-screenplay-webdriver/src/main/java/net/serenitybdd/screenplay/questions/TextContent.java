package net.serenitybdd.screenplay.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

import java.util.List;
import java.util.stream.Collectors;

public class TextContent extends TargetedUIState<String> {

    public TextContent(Target target, Actor actor) {
        super(target, actor);
    }

    public static UIStateReaderBuilder<TextContent> of(Target target) {
        return new UIStateReaderBuilder<>(target, TextContent.class);
    }

    public static UIStateReaderBuilder<TextContent> of(By byLocator) {
        return new UIStateReaderBuilder<>(Target.the(byLocator.toString()).located(byLocator), TextContent.class);
    }

    public static UIStateReaderBuilder<TextContent> of(String locator) {
        return new UIStateReaderBuilder<>(Target.the(locator).locatedBy(locator), TextContent.class);
    }

    public String resolve() {
        return target.resolveFor(actor).getAttribute("textContent");
    }

    public List<String> resolveAll() {
        return target.resolveAllFor(actor).stream()
                .map(element -> element.getAttribute("textContent"))
                .collect(Collectors.toList());
    }
}
