package net.serenitybdd.screenplay.shopping.tasks;

import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;

public class BrokenAnonymousPerformable {

    public static Performable thatThrowsAnException() {
        return Task.where("{0} does something that will throw an Exception",
                actor -> {
                    throw new IllegalArgumentException("This is a broken task");
                }
        );
    }

    public static Performable thatThrowsAnAssertionError() {
        return Task.where("{0} does something that will throw an AssertionError",
                actor -> {
                    throw new AssertionError("This is a broken task");
                }
        );
    }
}
