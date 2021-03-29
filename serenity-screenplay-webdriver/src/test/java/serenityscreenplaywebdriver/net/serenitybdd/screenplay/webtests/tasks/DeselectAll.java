package serenityscreenplaywebdriver.net.serenitybdd.screenplay.webtests.tasks;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Performable;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions.deselectactions.DeselectAllOptions;
import serenitymodel.net.thucydides.core.annotations.Step;

import static serenityscreenplaywebdriver.net.serenitybdd.screenplay.webtests.pages.ProfilePage.CONTACT_PREFERENCES;

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
