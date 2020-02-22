package net.serenitybdd.screenplay;

import net.serenitybdd.core.steps.Instrumented;

import static java.util.Arrays.asList;

/**
 * A backend task will report on all nested tasks, but will not generate any screenshots.
 */
public class BackendTask {
    public static <T extends Performable> AnonymousTask where(String title, T... steps) {
        return Instrumented.instanceOf(AnonymousBackendTask.class)
                           .withProperties(title, asList(steps)).withNoReporting();
    }

    public static <T extends Performable> AnonymousTask where(T... steps) {
        return Instrumented.instanceOf(AnonymousBackendTask.class)
                           .withProperties("Backend task",  asList(steps)).withNoReporting();
    }
}
