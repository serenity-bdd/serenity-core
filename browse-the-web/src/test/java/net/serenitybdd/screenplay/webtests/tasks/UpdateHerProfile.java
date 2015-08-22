package net.serenitybdd.screenplay.webtests.tasks;

import net.serenitybdd.screenplay.tasks.Settable;
import net.serenitybdd.screenplay.webtests.pages.ProfilePage;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class UpdateHerProfile {

    public static Settable name() {
        return instrumented(UpdateProfileFieldValue.class, ProfilePage.NAME);
    }

    public static Settable country() {
        return instrumented(UpdateProfileOption.class, ProfilePage.COUNTRY);
    }

}
