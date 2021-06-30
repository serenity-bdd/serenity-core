package net.serenitybdd.screenplay;

import net.thucydides.core.steps.StepEventBus;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;

/**
 * Run a series of Performable tasks in parallel with different actors
 */
public class InParallel {

    Actor[] cast;

    private InParallel(Actor[] actors) {
        cast = actors;
    }

    /**
     * Perform the specified tasks in parallel.
     * For example:
     * <pre>
     *     <code>
     *         InParallel.theActors(johny, gina, jimmy).perform(
     *                 () -> johnny.attemptsTo(BookFlight.from("New York).to("London")),
     *                 () -> gina.attemptsTo(BookFlight.from("New York).to("Los Angeles")),
     *                 () -> jimmy.attemptsTo(BookFlight.from("Sydney).to("Hong Kong")),
     *         );
     *     </code>
     * </pre>
     */
    public static InParallel theActors(Actor... actors) {
        return new InParallel(actors);
    }

    public static InParallel theActors(Collection<Actor> actors) {
        return new InParallel(actors.toArray(new Actor[]{}));
    }

    public void perform(List<Runnable> tasks) {
        perform(tasks.toArray(new Runnable[]{}));
    }

    public void perform(Runnable... tasks) {
        try {
            StepEventBus.getEventBus().registerAgents(cast);
            asList(tasks).parallelStream().forEach(Runnable::run);
        } finally {
            StepEventBus.getEventBus().mergeActivitiesToDefaultStepListener(cast);
            StepEventBus.getEventBus().dropAgents(cast);
        }
    }

    /**
     * Have several actors perform a given task in parallel, for example:
     * <pre>
     *     <code>
     *     InParallel.theActors(johny, gina, jimmy).eachAttemptTo(BookFlight.from("New York).to("London"));
     *     </code>
     * </pre>
     */
    public void eachAttemptTo(Performable... tasks) {
        List<Runnable> runnableTasks = stream(cast)
                .map(actor -> (Runnable) () -> actor.attemptsTo(tasks))
                .collect(Collectors.toList());

        perform(runnableTasks.toArray(new Runnable[]{}));
    }

    public void eachAttemptTo(Collection<Performable> tasks) {
        eachAttemptTo(tasks.toArray(new Performable[]{}));
    }
}
