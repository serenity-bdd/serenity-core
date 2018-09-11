package net.serenitybdd.screenplay.webtests.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.actions.deselectactions.DeselectAllOptions;
import net.thucydides.core.annotations.Step;

import static net.serenitybdd.screenplay.webtests.pages.ProfilePage.CONTACT_PREFERENCES;

public class DeselectAll implements Performable {


    public DeselectAll() {}

    public static DeselectAll contactPreferences() {
        return new DeselectAll();
    }

    @Step("{0} deselects all options from #CONTACT_PREFERENCES")
    public <T extends Actor> void performAs(T theUser) {
        theUser.attemptsTo(DeselectAllOptions.from(CONTACT_PREFERENCES));
    }

}
