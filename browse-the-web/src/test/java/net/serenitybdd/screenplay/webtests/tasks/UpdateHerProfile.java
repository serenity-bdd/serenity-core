package net.serenitybdd.screenplay.webtests.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.tasks.Enter;
import net.serenitybdd.screenplay.webtests.pages.ProfilePage;
import net.thucydides.core.annotations.Step;

import static net.serenitybdd.screenplay.webtests.pages.ProfilePage.ProfileField.Name;
import static net.serenitybdd.screenplay.webtests.pages.ProfilePage.the;

public class UpdateHerProfile implements Performable {

    ProfilePage.ProfileField field;
    String newValue = "";

    @Step("When {0} updates her #field to #newValue")
    public <T extends Actor> void performAs(T theUser) {
        theUser.attemptsTo(Enter.theValue(newValue).into(the(field)));
    }



    public UpdateHerProfile name() {
        this.field = Name;
        return this;
    }

    public Performable to(String newValue) {
        this.newValue = newValue;
        return this;
    }

}
