package serenityscreenplaywebdriver.net.serenitybdd.screenplay.webtests.tasks;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Performable;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions.DeselectFromOptions;
import serenitymodel.net.thucydides.core.annotations.Step;

import static serenityscreenplaywebdriver.net.serenitybdd.screenplay.webtests.pages.ProfilePage.CONTACT_PREFERENCES;

public class DeselectContactPreference implements Performable {

    private String existingValue;


    public DeselectContactPreference() {
    }

    private DeselectContactPreference(String existingValue) {
        this.existingValue = existingValue;
    }

    public static DeselectContactPreference withText(String existingValue) {
        return new DeselectContactPreference(existingValue);
    }

    @Step("{0} deselects #existingValue from #CONTACT_PREFERENCES")
    public <T extends Actor> void performAs(T theUser) {
        theUser.attemptsTo(DeselectFromOptions.byVisibleText(existingValue).from(CONTACT_PREFERENCES));
    }

}
