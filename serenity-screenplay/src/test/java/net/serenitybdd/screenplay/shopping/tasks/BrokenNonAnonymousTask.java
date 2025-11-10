package net.serenitybdd.screenplay.shopping.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

public class BrokenNonAnonymousTask implements Task {

    boolean assertionFailure = false;

    public BrokenNonAnonymousTask(boolean assertionFailure) {
        this.assertionFailure = assertionFailure;
    }

    public static BrokenNonAnonymousTask thatThrowsAnException() {
        return new BrokenNonAnonymousTask(false);
    }

    public static BrokenNonAnonymousTask thatThrowsAnAssertionError() {
        return new BrokenNonAnonymousTask(true);
    }

    @Override
    public void performAs(Actor actor) {
        // No anonymous performable function here.
        if (assertionFailure) {
            throw new AssertionError("This is a broken task");
        } else {
            throw new IllegalArgumentException("This is a broken task");
        }
    }
}
