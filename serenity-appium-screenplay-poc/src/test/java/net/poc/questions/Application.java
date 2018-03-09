package net.poc.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.poc.ui.AddNotePage;

public class Application implements Question<String> {
    @Override
    public String answeredBy(Actor actor) {
        return AddNotePage.SNACKBAR.resolveFor(actor).getText();
    }

    public static Application notification() {
        return new Application();
    }
}
