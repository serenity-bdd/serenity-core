package net.serenitybdd.screenplay.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.targets.Target;

import java.util.List;
import java.util.stream.Collectors;

public class TextContent extends TargetedUIState<String> {

    public TextContent(Target target, Actor actor) {
        super(target, actor);
    }

    public static UIStateReaderBuilder<Text> of(Target target) {
        return new UIStateReaderBuilder<>(target, Text.class);
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
