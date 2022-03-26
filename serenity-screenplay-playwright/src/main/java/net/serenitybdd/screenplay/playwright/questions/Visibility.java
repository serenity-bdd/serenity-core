package net.serenitybdd.screenplay.playwright.questions;

import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

public class Visibility {

    public static Question<Boolean> of(Target target) {
        return Question.about("visibility of " + target.toString()).answeredBy(actor ->
            BrowseTheWebWithPlaywright.as(actor).getCurrentPage().isVisible(target.asSelector())
        );
    }

    public static Question<Boolean> of(String locator) {
        return Question.about("visibility of " + locator).answeredBy(actor ->
            BrowseTheWebWithPlaywright.as(actor).getCurrentPage().isVisible(locator)
        );
    }
}
