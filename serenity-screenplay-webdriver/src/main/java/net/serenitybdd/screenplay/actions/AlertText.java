package net.serenitybdd.screenplay.actions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;

import java.util.Optional;

/**
 * Retrieve the text of the current alert window
 */
public class AlertText implements Question<String> {

    Optional<Performable> thenPerform = Optional.empty();

    public static AlertText currentlyVisible() {
        return new AlertText();
    }

    public AlertText thenDismiss() {
        this.thenPerform = Optional.of(Switch.toAlert().andDismiss());
        return this;
    }

    public AlertText thenAccept() {
        this.thenPerform = Optional.of(Switch.toAlert().andAccept());
        return this;
    }

    @Override
    public String answeredBy(Actor actor) {
        String alert = BrowseTheWeb.as(actor).getDriver().switchTo().alert().getText();
        thenPerform.ifPresent(actor::attemptsTo);
        return alert;
    }
}
