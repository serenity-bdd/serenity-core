package net.serenitybdd.screenplay.webtests.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.actions.Settable;
import net.serenitybdd.screenplay.webtests.pages.ProfilePage;
import net.serenitybdd.annotations.Step;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class UpdateHerProfile implements Performable {

    String name = "";
    String countryOfResidence = "UK";
    String dob = "";
    String color = "Red";

    public UpdateHerProfile(String name) {
        this.name = name;
    }

    public static Settable name() {
        return instrumented(UpdateProfileFieldValue.class, ProfilePage.NAME);
    }

    public static Settable country() {
        return instrumented(UpdateProfileOption.class, ProfilePage.COUNTRY);
    }

    public static Settable dob() {
        return instrumented(UpdateProfileFieldValue.class, ProfilePage.DATE_OF_BIRTH);
    }

    public static Settable color() {
        return instrumented(UpdateProfileOption.class, ProfilePage.COLOR);
    }

    public static UpdateHerProfile withName(String name) {
        return instrumented(UpdateHerProfile.class, name);
    }

    public UpdateHerProfile andCountryOfResidence(String countryOfResidence) {
        this.countryOfResidence = countryOfResidence;
        return this;
    }

    public UpdateHerProfile andFavoriteColor(String color) {
        this.color = color;
        return this;
    }

    public UpdateHerProfile andDateOfBirth(String dob) {
        this.dob = dob;
        return this;
    }

    @Step("{0} updates her profile details")
    public <T extends Actor> void performAs(T theUser) {
        theUser.attemptsTo(
                UpdateHerProfile.name().to(name),
                UpdateHerProfile.country().to(countryOfResidence),
                UpdateHerProfile.dob().to(dob),
                UpdateHerProfile.color().to(color)
        );

        ProfilePage.INVISIBLE.resolveFor(theUser).waitUntilNotVisible();
        ProfilePage.INEXISTANT.resolveFor(theUser).waitUntilNotVisible();
    }

}
