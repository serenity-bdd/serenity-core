package net.serenitybdd.screenplay.playwright.questions;

import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

/**
 * Questions about the current web page state.
 */
public class TheWebPage {

    /**
     * Returns the page's title.
     */
    public static Question<String> title() {
        return Question.about("the page title").answeredBy(
            actor -> BrowseTheWebWithPlaywright.as(actor).getCurrentPage().title()
        );
    }

    /**
     * Returns the current page URL.
     */
    public static Question<String> currentUrl() {
        return Question.about("the current URL").answeredBy(
            actor -> BrowseTheWebWithPlaywright.as(actor).getCurrentPage().url()
        );
    }

    /**
     * Returns the page's HTML source.
     */
    public static Question<String> source() {
        return Question.about("the page source").answeredBy(
            actor -> BrowseTheWebWithPlaywright.as(actor).getCurrentPage().content()
        );
    }
}
