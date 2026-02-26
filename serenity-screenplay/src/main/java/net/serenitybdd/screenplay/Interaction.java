package net.serenitybdd.screenplay;

import net.serenitybdd.core.steps.Instrumented;

import java.util.Arrays;
import java.util.function.Consumer;

/**
 * A marker class to indicate that a Performable represents a system interaction (action),
 * rather than a business task.
 */
public interface Interaction extends Performable {
    static <T extends Performable> AnonymousInteraction where(T... steps) {
        return Instrumented.instanceOf(AnonymousInteraction.class).withProperties(HumanReadableTaskName.forCurrentMethod(), Arrays.asList(steps));
    }

    static InteractionBuilder called(String title) { return new InteractionBuilder(title); }

    /**
     * Create a new Performable Interaction made up of a list of Performables.
     */
    static <T extends Performable> AnonymousInteraction where(String title, T... steps) {
        return Instrumented.instanceOf(AnonymousInteraction.class).withProperties(title, Arrays.asList(steps));
    }

    static <T extends Performable> AnonymousInteractionFunction where(Consumer<Actor> performableOperation) {
        return Instrumented.instanceOf(AnonymousInteractionFunction.class).withProperties(HumanReadableTaskName.forCurrentMethod(),
                                                                                          performableOperation);
    }

    static <T extends Performable> AnonymousInteractionFunction where(String title, Consumer<Actor> performableOperation) {
        return Instrumented.instanceOf(AnonymousInteractionFunction.class).withProperties(title, performableOperation);
    }

    static <T extends Performable> AnonymousPerformableRunnable thatPerforms(Runnable performableOperation) {
        return Instrumented.instanceOf(AnonymousPerformableRunnable.class).withProperties(HumanReadableTaskName.forCurrentMethod(), performableOperation);
    }

    static <T extends Performable> AnonymousPerformableRunnable thatPerforms(String title, Runnable performableOperation) {
        return Instrumented.instanceOf(AnonymousPerformableRunnable.class).withProperties(title, performableOperation);
    }

}
