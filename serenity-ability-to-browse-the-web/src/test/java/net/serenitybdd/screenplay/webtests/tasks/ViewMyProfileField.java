package net.serenitybdd.screenplay.webtests.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.annotations.Subject;
import net.serenitybdd.screenplay.webtests.pages.ProfilePage;

import static net.serenitybdd.screenplay.webtests.pages.ProfilePage.ProfileField.Name;

@Subject("#field")
public class ViewMyProfileField implements Question<String> {

    private ProfilePage profilePage;

    private ProfilePage.ProfileField field;

    public Question<String> name() {
        this.field = Name;
        return this;
    }

    public String answeredBy(Actor actor) {
        profilePage = BrowseTheWeb.as(actor).onPage(ProfilePage.class);
        return profilePage.fieldValueFor(field);
    }
}
