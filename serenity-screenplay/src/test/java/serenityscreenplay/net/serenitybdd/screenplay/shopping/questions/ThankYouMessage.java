package serenityscreenplay.net.serenitybdd.screenplay.shopping.questions;

import serenityscreenplay.net.serenitybdd.screenplay.shopping.PeopleAreSoRude;
import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Question;
import serenityscreenplay.net.serenitybdd.screenplay.QuestionDiagnostics;

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
