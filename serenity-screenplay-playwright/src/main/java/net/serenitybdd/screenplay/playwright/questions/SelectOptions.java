package net.serenitybdd.screenplay.playwright.questions;

import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

import java.util.List;

/**
 * Get a list of select options from a dropdown
 */
public class SelectOptions {

    public static Question<List<String>> of(Target target) {
        return of(target.asSelector());
    }

    public static Question<List<String>> of(String locator) {
        return Question.about("options of " + locator).answeredBy(actor ->
            BrowseTheWebWithPlaywright.as(actor).getCurrentPage().locator(locator + " > option").allInnerTexts()
        );
    }
}