package net.serenitybdd.screenplay.playwright.questions;

import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

public class TheWebPage {
    /**
     * Returns the page's title
     */
    public static Question<String> title() {
        return Question.about("the page title").answeredBy(
                actor -> BrowseTheWebWithPlaywright.as(actor).getCurrentPage().title()
        );
    }
}
