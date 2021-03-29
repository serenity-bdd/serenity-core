package serenityscreenplaywebdriver.net.serenitybdd.screenplay.webtests.questions;

import serenitycore.net.serenitybdd.core.pages.WebElementFacade;
import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Question;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.webtests.pages.ProfilePage;

import java.util.Set;

public class ContactPreferences {

    public static Question<Set<String>> nowSelected() {
        return new Question<Set<String>>() {
            @Override
            public Set<String> answeredBy(Actor actor) {
                WebElementFacade webElementFacade = ProfilePage.CONTACT_PREFERENCES.resolveFor(actor);
                return BrowseTheWeb.as(actor).getSelectedOptionLabelsFrom(webElementFacade);
            }
        };
    }
}
