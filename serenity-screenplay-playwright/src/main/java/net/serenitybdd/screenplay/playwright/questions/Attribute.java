package net.serenitybdd.screenplay.playwright.questions;

import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

/**
 * Get attribute of an element. More info: https://playwright.dev/java/docs/api/class-page#page-get-attribute.
 */
public class Attribute {

    public static Question<String> of(Target target, String attributeName) {
        return of(target.asSelector(), attributeName);
    }

    public static Question<String> of(String locator, String attributeName) {
        return Question.about(attributeName + " attribute of " + locator).answeredBy(actor ->
            BrowseTheWebWithPlaywright.as(actor).getCurrentPage().getAttribute(locator, attributeName)
        );
    }
}