package net.serenitybdd.screenplay.questions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.targets.Target;

import java.util.stream.Collectors;

public class Text extends TargetedUIState<String> {

    public Text(Target target, Actor actor) {
        super(target, actor);
    }

    public static UIStateReaderBuilder<Text> of(Target target) {
        return new UIStateReaderBuilder<>(target, Text.class);
    }

    public String resolve() {
        return target.resolveFor(actor).getText();
    }

    public java.util.List<String> resolveAll() {
        return target.resolveAllFor(actor).stream()
                     .map(WebElementFacade::getText)
                     .collect(Collectors.toList());
    }
}
