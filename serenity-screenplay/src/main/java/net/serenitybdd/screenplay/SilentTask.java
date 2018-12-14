package net.serenitybdd.screenplay;

import net.serenitybdd.core.steps.Instrumented;

import java.util.Arrays;

import static java.util.Arrays.asList;

public class SilentTask {
    public static <T extends Performable> AnonymousTask where(String title, T... steps) {
        return Instrumented.instanceOf(AnonymousTask.class)
                           .withProperties(title, asList(steps)).withNoReporting();
    }

    public static <T extends Performable> AnonymousTask where(T... steps) {
        return Instrumented.instanceOf(AnonymousTask.class)
                           .withProperties("Anonymous task",  asList(steps)).withNoReporting();
    }
}
