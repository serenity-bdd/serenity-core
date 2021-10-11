package net.serenitybdd.screenplay.waits;

import net.serenitybdd.screenplay.Question;
import org.hamcrest.Matcher;

import java.util.concurrent.Callable;

public class Wait {

    public static WaitWithTimeout until(Question question, Matcher matcher) {
        return new WaitOnQuestion(question, matcher);
    }

    public static WaitWithTimeout until(Callable<Boolean> expectedState) {
        return new WaitOnSupplier(expectedState);
    }

}
