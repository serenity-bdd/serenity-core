package net.serenitybdd.screenplay.shopping.questions;

import net.serenitybdd.screenplay.shopping.PeopleAreSoRude;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.QuestionDiagnostics;

public class ThankYouMessage implements QuestionDiagnostics, Question<String> {

    @Override
    public String answeredBy(Actor actor) {
        return "Thank you!";
    }

    public static ThankYouMessage theThankYouMessage() {
        return new ThankYouMessage();
    }

    public Class<? extends AssertionError> onError() { return PeopleAreSoRude.class; }

}
