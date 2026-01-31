package net.serenitybdd.screenplay.playwright.questions;

import com.microsoft.playwright.Page;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

import java.util.List;

/**
 * Return the inner text value of an element.
 */
public class Text {
    public static Question<String> of(Target target) {
        return Question.about("the text value of " + target).answeredBy(
            actor -> {
                Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
                return target.resolveFor(page).innerText();
            }
        );
    }

    public static Question<String> of(String selector) {
        return of(Target.the(selector).locatedBy(selector));
    }

    public static Question<List<String>> ofEach(Target target) {
        return Question.about("the text values of " + target).answeredBy(
            actor -> {
                Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
                return target.resolveFor(page).allInnerTexts();
            }
        );
    }

    public static Question<List<String>> ofEach(String selector) {
        return ofEach(Target.the(selector).locatedBy(selector));
    }
}
