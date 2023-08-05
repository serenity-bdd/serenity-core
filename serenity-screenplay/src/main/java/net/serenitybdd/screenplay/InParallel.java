package net.serenitybdd.screenplay;

import net.serenitybdd.model.exceptions.SerenityManagedException;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.model.domain.TestStep;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.model.util.EnvironmentVariables;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

/**
 * Run a series of Performable tasks in parallel with different actors
 */
public class InParallel {

    Actor[] cast;
    EnvironmentVariables environmentVariables;

    private InParallel(Actor[] actors, EnvironmentVariables environmentVariables) {
        cast = actors;
        this.environmentVariables = environmentVariables;
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
        return new InParallel(actors, SystemEnvironmentVariables.currentEnvironmentVariables());
    }

    /**
     * Useful if you have a collection or cast of actors.
     */
    public static InParallel theActors(Collection<Actor> actors) {
        return new InParallel(actors.toArray(new Actor[]{}), SystemEnvironmentVariables.currentEnvironmentVariables());
    }

    public void perform(List<Runnable> tasks) {
        perform(tasks.toArray(new Runnable[]{}));
    }

    public void perform(Runnable... tasks) {
        perform("{0}", tasks);
    }

    public void perform(String stepName, Runnable... tasks) {
        try {
            StepEventBus.getParallelEventBus().registerAgents(cast);
            ExecutorService executorService = Executors.newFixedThreadPool(environmentVariables.getPropertyAsInteger("screenplay.max.parallel.tasks", 16));
            List<Future<?>> futures = stream(tasks).map( task -> executorService.submit(task)).collect(Collectors.toList());

            futures.forEach(future -> {
                try {
                    future.get();
                } catch (ExecutionException | InterruptedException e) {
                    if (e.getCause() instanceof AssertionError) {
                        throw (AssertionError) e.getCause();
                    } else if (e.getCause() instanceof Error) {
                        throw (Error) e.getCause();
                    }
                    throw new SerenityManagedException("An error occurred in one of the parallel tasks", e.getCause());
                }
            });
        } finally {
            StepEventBus.getParallelEventBus().mergeActivitiesToDefaultStepListener(stepName, cast);
            StepEventBus.getParallelEventBus().dropAgents(cast);
            firstFailingStep().ifPresent(
                    step -> {
                        StepEventBus.getParallelEventBus().testFailed(step.getException().asException());
                        StepEventBus.getParallelEventBus().suspendTest();
                    }
            );
        }
    }

    private Optional<TestStep> firstFailingStep() {
        return StepEventBus.getParallelEventBus().getBaseStepListener().latestTestOutcome().get().getFlattenedTestSteps().stream()
                                  .filter(step -> step.getException() != null)
                                  .findFirst();
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
