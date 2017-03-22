package net.serenitybdd.screenplay.webtests.questions;

import java.util.Set;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.webtests.pages.ProfilePage;

public class CountryQuestion {
    public static Question<Set<String>> nowSelected() {
        return new Question<Set<String>>() {
            @Override
            public Set<String> answeredBy(Actor actor) {
                WebElementFacade webElementFacade = ProfilePage.COUNTRY.resolveFor(actor);
                return BrowseTheWeb.as(actor).getSelectedOptionLabelsFrom(webElementFacade);
            }
        };
    }
}
