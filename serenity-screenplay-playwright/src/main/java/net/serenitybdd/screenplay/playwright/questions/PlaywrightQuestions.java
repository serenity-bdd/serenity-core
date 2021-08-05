package net.serenitybdd.screenplay.playwright.questions;

import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

public class PlaywrightQuestions {
    /**
     * Returns the page's title
     */
    public static Question<String> pageTitle() {
        return Question.about("the page title")
                .answeredBy(
                        actor -> BrowseTheWebWithPlaywright.as(actor).getCurrentPage().title()
                );
    }

    /**
     * Return the inner text value of an element.
     */
    public static Question<String> textOf(Target element) {
        return textOf(element.asSelector());
    }

    public static Question<String> textOf(String selector) {
        return Question.about("the text value of " + selector)
                .answeredBy(
                        actor -> BrowseTheWebWithPlaywright.as(actor).getCurrentPage().innerText(selector)
                );
    }

}
