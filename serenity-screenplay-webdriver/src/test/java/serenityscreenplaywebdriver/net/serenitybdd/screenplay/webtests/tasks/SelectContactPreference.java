package serenityscreenplaywebdriver.net.serenitybdd.screenplay.webtests.tasks;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Performable;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions.SelectFromOptions;
import serenitymodel.net.thucydides.core.annotations.Step;

import static serenityscreenplaywebdriver.net.serenitybdd.screenplay.webtests.pages.ProfilePage.CONTACT_PREFERENCES;

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
