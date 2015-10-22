package net.serenitybdd.journey.shopping.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

public class ThankYouMessage implements Question<String> {

    @Override
    public String answeredBy(Actor actor) {
        return "Thank you!";
    }

    public static ThankYouMessage theThankYouMessage() {
        return new ThankYouMessage();
    }
}
