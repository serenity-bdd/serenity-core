package net.serenitybdd.screenplay.shopping.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.Task;

public class BrokenQuestion implements Question<Integer> {

    boolean assertionFailure = false;

    public BrokenQuestion(boolean assertionFailure) {
        this.assertionFailure = assertionFailure;
    }

    public static BrokenQuestion thatThrowsAnException() {
        return new BrokenQuestion(false);
    }

    public static BrokenQuestion thatThrowsAnAssertionError() {
        return new BrokenQuestion(true);
    }

    @Override
    public Integer answeredBy(Actor actor) {
        actor.attemptsTo(
                Task.where("{0} attempts to do something that will throw an exception",
                        actor1 -> {
                            if (assertionFailure) {
                                throw new AssertionError("This is a broken question");
                            } else {
                                throw new IllegalArgumentException("This is a broken question");
                            }
                        }
                )
        );
        return 10;
    }
}
