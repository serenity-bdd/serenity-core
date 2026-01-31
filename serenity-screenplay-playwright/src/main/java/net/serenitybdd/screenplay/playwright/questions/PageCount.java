package net.serenitybdd.screenplay.playwright.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.annotations.Subject;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

/**
 * Get the number of open pages (tabs) in the browser context.
 */
@Subject("the number of open pages")
public class PageCount implements Question<Integer> {

    public static Question<Integer> inTheBrowser() {
        return new PageCount();
    }

    @Override
    public Integer answeredBy(Actor actor) {
        BrowseTheWebWithPlaywright ability = BrowseTheWebWithPlaywright.as(actor);
        return ability.getAllPages().size();
    }
}
