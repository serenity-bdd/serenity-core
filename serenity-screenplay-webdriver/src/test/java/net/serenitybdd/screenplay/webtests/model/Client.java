package net.serenitybdd.screenplay.webtests.model;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.annotations.Subject;
import net.serenitybdd.screenplay.questions.SelectedVisibleTextValue;
import net.serenitybdd.screenplay.questions.Value;
import net.serenitybdd.screenplay.webtests.pages.ProfilePage;

public class Client {

    public static Question<String> name() {
        return new Question<String>() {
            @Override
            @Subject("name")
            public String answeredBy(Actor actor) {
                return Value.of(ProfilePage.NAME).viewedBy(actor).asString();
            }
        };
    }

    public static Question<String> color() {
        return new Question<String>() {
            public String answeredBy(Actor actor) {
                return SelectedVisibleTextValue.of(ProfilePage.COLOR).viewedBy(actor).asString();
            }

            public String getSubject() {
                return "her favorite colour";
            }
        };
    }

    public static Question<String> dateOfBirth() {
        return new Question<String>() {
            @Override
            @Subject("date of birth")
            public String answeredBy(Actor actor) {
                return Value.of(ProfilePage.DATE_OF_BIRTH).viewedBy(actor).asString();
            }
        };
    }

    public static Question<String> country() {
        return new Question<String>() {
            @Override
            @Subject("country")
            public String answeredBy(Actor actor) {
                return SelectedVisibleTextValue.of(ProfilePage.COUNTRY).viewedBy(actor).asString();
            }
        };
    }

}
