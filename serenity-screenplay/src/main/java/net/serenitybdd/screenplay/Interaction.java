package net.serenitybdd.screenplay;

import net.serenitybdd.core.steps.Instrumented;

import java.util.Arrays;
import java.util.function.Consumer;

/**
 * A marker class to indicate that a Performable represents a system interaction (action),
 * rather than a business task.
 */
@SuppressWarnings("deprecation")
public interface Interaction extends Performable {
    static AnonymousInteraction where(String title, Performable... steps) {
        return Instrumented.instanceOf(AnonymousInteraction.class).withProperties(title, Arrays.asList(steps));
    }

    static AnonymousInteraction where(String title, Consumer<Actor> performableOperation) {
        return Instrumented.instanceOf(AnonymousInteraction.class).withProperties(title, performableOperation);
    }

    static AnonymousInteraction thatPerforms(String title, Runnable performableOperation) {
        return Instrumented.instanceOf(AnonymousInteraction.class).withProperties(title, performableOperation);
    }

}
