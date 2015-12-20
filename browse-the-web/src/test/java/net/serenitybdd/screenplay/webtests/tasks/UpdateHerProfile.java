package net.serenitybdd.screenplay.webtests.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.actions.Settable;
import net.serenitybdd.screenplay.webtests.pages.ProfilePage;
import net.thucydides.core.annotations.Step;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class UpdateHerProfile implements Performable {

    String name;
    String countryOfResidence;

    public UpdateHerProfile(String name) {
        this.name = name;
    }

    public static Settable name() {
        return instrumented(UpdateProfileFieldValue.class, ProfilePage.NAME);
    }

    public static Settable country() {
        return instrumented(UpdateProfileOption.class, ProfilePage.COUNTRY);
    }

    public static UpdateHerProfile withName(String name) {
        return instrumented(UpdateHerProfile.class, name);
    }

    public UpdateHerProfile andCountryOfResidence(String countryOfResidence) {
        this.countryOfResidence = countryOfResidence;
        return this;
    }

    @Step("{0} updates her profile details")
    public <T extends Actor> void performAs(T theUser) {
        theUser.attemptsTo(UpdateHerProfile.name().to(name));
        theUser.attemptsTo(UpdateHerProfile.country().to(countryOfResidence));

    }
}
