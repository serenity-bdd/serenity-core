package net.serenitybdd.screenplay;

import net.serenitybdd.core.steps.Instrumented;

import java.util.Arrays;

/**
 * A marker class to indicate that a Performable represents a higher level business task,
 * rather than a system interaction.
 */
public interface Task extends Performable {
    static <T extends Performable> AnonymousTask where(String title, T... steps) {
        return Instrumented.instanceOf(AnonymousTask.class).withProperties(title, Arrays.asList(steps));
    }
}
