package net.serenitybdd.screenplay.playwright.questions;

import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

import java.util.List;

/**
 * Return the inner text value of an element.
 */
public class Text {
    public static Question<String> of(Target element) {
        return of(element.asSelector());
    }

    public static Question<String> of(String selector) {
        return Question.about("the text value of " + selector).answeredBy(
                actor -> BrowseTheWebWithPlaywright.as(actor).getCurrentPage().innerText(selector)
        );
    }

    public static Question<List<String>> ofEach(Target element) {
        return ofEach(element.asSelector());
    }

    public static Question<List<String>> ofEach(String selector) {
        return Question.about("the text values of " + selector).answeredBy(
                actor -> BrowseTheWebWithPlaywright.as(actor).getCurrentPage().locator(selector).allInnerTexts()
        );
    }

}
