package net.serenitybdd.screenplay.webtests.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.actions.SelectFromOptions;
import net.thucydides.core.annotations.Step;

import static net.serenitybdd.screenplay.webtests.pages.ProfilePage.CONTACT_PREFERENCES;

public class SelectContactPreference implements Performable {

    String value;

    public SelectContactPreference() {
    }

    private SelectContactPreference(String value) {
        this.value = value;
    }

    public static SelectContactPreference withText(String value) {
        return new SelectContactPreference(value);
    }

    @Step("{0} selects #value from #CONTACT_PREFERENCES")
    public <T extends Actor> void performAs(T theUser) {
        theUser.attemptsTo(SelectFromOptions.byVisibleText(value).from(CONTACT_PREFERENCES));
    }

}
