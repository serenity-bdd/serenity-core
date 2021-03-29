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
     *
     * @param element
     * @return
     */
    public static Question<String> textOf(Target element) {
        return Question.about("the text value of " + element)
                .answeredBy(
                        actor -> BrowseTheWebWithPlaywright.as(actor).getCurrentPage().innerText(element.asSelector())
                );

    }
}
