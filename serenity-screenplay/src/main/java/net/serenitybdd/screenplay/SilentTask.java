package net.serenitybdd.screenplay;

import net.serenitybdd.core.steps.Instrumented;

import java.util.Arrays;

public class SilentTask {
    static <T extends Performable> AnonymousTask where(String title, T... steps) {
        return Instrumented.instanceOf(AnonymousTask.class).withProperties(title, Arrays.asList(steps)).withNoReporting();
    }

}
