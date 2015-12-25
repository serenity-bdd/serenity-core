package net.serenitybdd.screenplay.webtests.tasks;

import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.webtests.pages.ProfilePage;
import net.serenitybdd.screenplay.webtests.questions.ReadProfileField;
import net.serenitybdd.screenplay.webtests.questions.ReadProfileOptionValue;


public class TheProfile {

    public static Question<String> name() {
        return new ReadProfileField(ProfilePage.NAME);
    }

    public static Question<String> country() {
        return new ReadProfileOptionValue(ProfilePage.COUNTRY);
    }

}
