package net.serenitybdd.screenplay.playwright.questions;

import com.microsoft.playwright.Page;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

public class Visibility {

    public static Question<Boolean> of(Target target) {
        return Question.about("visibility of " + target.toString()).answeredBy(actor -> {
            Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
            return target.resolveFor(page).isVisible();
        });
    }

    public static Question<Boolean> of(String selector) {
        return of(Target.the(selector).locatedBy(selector));
    }
}
