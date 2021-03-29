package serenityscreenplay.net.serenitybdd.screenplay.shopping.questions;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Question;
import serenityscreenplay.net.serenitybdd.screenplay.QuestionDiagnostics;
import serenityscreenplay.net.serenitybdd.screenplay.shopping.PeopleAreSoRude;

import static serenityscreenplay.net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static serenityscreenplay.net.serenitybdd.screenplay.shopping.questions.ThankYouMessage.theThankYouMessage;
import static org.hamcrest.Matchers.equalTo;

public class NestedThankYouMessage implements QuestionDiagnostics, Question<String> {

    @Override
    public String answeredBy(Actor actor) {
        actor.should(
                seeThat(theThankYouMessage(), equalTo("You're welcome")),
                seeThat(theThankYouMessage(), equalTo("No problem")),
                seeThat(theThankYouMessage(), equalTo("Thank you!"))
        );
        return ThankYouMessage.theThankYouMessage().answeredBy(actor);
    }

    public static NestedThankYouMessage theNestedThankYouMessage() {
        return new NestedThankYouMessage();
    }

    public Class<? extends AssertionError> onError() { return PeopleAreSoRude.class; }

}
