package net.serenitybdd.screenplay;

import net.serenitybdd.core.steps.Instrumented;

import java.util.Arrays;

/**
 * A marker class to indicate that a Performable represents a system interaction (action),
 * rather than a business task.
 */
@SuppressWarnings("deprecation")
public interface Interaction extends Performable {
    static AnonymousInteraction where(String title, Performable... steps) {
        return Instrumented.instanceOf(AnonymousInteraction.class).withProperties(title, Arrays.asList(steps));
    }
}
