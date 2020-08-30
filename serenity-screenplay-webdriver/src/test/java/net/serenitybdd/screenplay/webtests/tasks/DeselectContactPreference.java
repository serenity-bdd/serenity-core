package net.serenitybdd.screenplay.webtests.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.actions.DeselectFromOptions;
import net.thucydides.core.annotations.Step;

import static net.serenitybdd.screenplay.webtests.pages.ProfilePage.CONTACT_PREFERENCES;

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
