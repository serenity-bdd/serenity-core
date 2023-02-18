package net.serenitybdd.screenplay.parallel;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.InParallel;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestStep;
import net.thucydides.core.steps.StepEventBus;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SerenityRunner.class)
public class WhenRunningTasksInParallel {

    private List<String> completedTasks =  Collections.synchronizedList(new ArrayList<String>());

    private Performable doSomething() {
        return Task.where("{0} does something",
                (actor) -> {
                    completedTasks.add("Do something");
                }
        );
    }

    private Performable doAnotherThing() {
        return Task.where("{0} does another thing",
                (actor) -> {
                    completedTasks.add("Do another thing");
                }
        );
    }

    private Performable doAThing() {
        return Task.where("{0} does a thing",
                (actor) -> {
                    completedTasks.add("Do a thing");
                }
        );
    }

    private Performable doYetAnotherThing() {
        return Task.where("{0} does yet another thing",
                (actor) -> {
                    completedTasks.add("Do yet another thing");
                }
        );
    }

    private Performable doSomethingElse() {
        return Task.where("{0} does something else",
                (actor) -> {
                    completedTasks.add("Do something else");
                }
        );
    }

    private Performable doSomethingThatFails() {
        return Task.where("{0} does something that fails",
                (actor) -> assertThat(true).isFalse()
        );
    }

    private Performable doSomethingWithSubTasks() {
        return Task.where("{0} does something with subtasks",
                (actor) -> {
                    actor.attemptsTo(
                            doAThing(), doYetAnotherThing()
                    );
                }
        );
    }

    private Performable doSomethingSlowly() {
        return Task.where("{0} does something slowly",
                (actor) -> {
                    try {
                        Thread.sleep(500);
                        completedTasks.add("Do something slowly");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        );
    }

    @Test
    public void parallelTasksShouldRunWithASingleAction() {

        Actor actorA = Actor.named("Actor A");

        InParallel.theActors(actorA).perform("{0} performs",
                () -> actorA.attemptsTo(doSomething())
        );
        assertThat(completedTasks).containsExactlyInAnyOrder("Do something");
        assertThat(testSteps()).containsExactly("Actor A performs", "Actor A does something");
    }

    @Test
    public void parallelTasksShouldRunWithASequenceOfActions() {

        Actor actorA = Actor.named("Actor A");

        InParallel.theActors(actorA).perform(
                () -> actorA.attemptsTo(doSomething(), doSomethingElse())
        );

        assertThat(completedTasks).containsExactlyInAnyOrder("Do something", "Do something else");
        assertThat(testSteps()).containsExactly("Actor A", "Actor A does something", "Actor A does something else");
    }

    @Test
    public void aGroupOfActorsCanAllPerformTheSameTaskInParallel() {

        Actor actorA = Actor.named("Actor A");
        Actor actorB = Actor.named("Actor B");
        Actor actorC = Actor.named("Actor C");

        InParallel.theActors(actorA, actorB, actorC).eachAttemptTo(doSomething(), doSomethingElse());

        assertThat(testSteps()).containsExactly(
                "Actor A", "Actor A does something", "Actor A does something else",
                "Actor B", "Actor B does something", "Actor B does something else",
                "Actor C", "Actor C does something", "Actor C does something else");
    }

    @Test(expected = AssertionError.class)
    public void failingTasksShouldBeReported() {

        Actor actorA = Actor.named("Actor A");
        Actor actorB = Actor.named("Actor B");
        Actor actorC = Actor.named("Actor C");

        InParallel.theActors(actorA, actorB, actorC).eachAttemptTo(doSomething(), doSomethingThatFails());
        actorA.attemptsTo(doSomethingElse());

        assertThat(StepEventBus.getParallelEventBus().getBaseStepListener().latestTestOutcome().get().getResult()).isEqualTo(TestResult.FAILURE);
    }

    @Test
    public void actorsCanBeDefinedInACollection() {

        Actor actorA = Actor.named("Actor A");
        Actor actorB = Actor.named("Actor B");
        Actor actorC = Actor.named("Actor C");

        List<Actor> cast = Arrays.asList(actorA, actorB, actorC);
        InParallel.theActors(cast).eachAttemptTo(doSomething(), doSomethingElse());

        assertThat(testSteps()).containsExactly(
                "Actor A", "Actor A does something", "Actor A does something else",
                "Actor B", "Actor B does something", "Actor B does something else",
                "Actor C", "Actor C does something", "Actor C does something else");
    }

    @Test
    public void parallelStreamsOfSingleActionsShouldResultInSeparateSteps() {

        Actor actorA = Actor.named("Actor A");
        Actor actorB = Actor.named("Actor B");

        InParallel.theActors(actorA, actorB).perform(
                () -> actorA.attemptsTo(doSomething()),
                () -> actorB.attemptsTo(doAnotherThing())
        );

        assertThat(completedTasks).containsExactlyInAnyOrder("Do something", "Do another thing");
        assertThat(testSteps()).containsExactly("Actor A", "Actor A does something", "Actor B", "Actor B does another thing");
    }

    @Test
    public void parallelStreamsOfSingleActionsShouldResultInSeparateStepsInTheRightOrder() {

        Actor actorA = Actor.named("Actor A");
        Actor actorB = Actor.named("Actor B");

        InParallel.theActors(actorA, actorB).perform(
                () -> actorA.attemptsTo(doSomething()),
                () -> actorB.attemptsTo(doSomethingElse(), doAnotherThing())
        );

        assertThat(completedTasks).containsExactlyInAnyOrder("Do something", "Do something else", "Do another thing");
        assertThat(testSteps()).containsExactly("Actor A", "Actor A does something", "Actor B", "Actor B does something else", "Actor B does another thing");
    }

    @Test
    public void nestedParallelTasksShouldBeRecordedInTheRightOrder() {

        Actor actorA = Actor.named("Actor A");
        Actor actorB = Actor.named("Actor B");

        InParallel.theActors(actorA, actorB).perform(
                () -> actorA.attemptsTo(doSomething(), doSomethingWithSubTasks()),
                () -> actorB.attemptsTo(doSomethingElse(), doSomethingWithSubTasks(), doAnotherThing())
        );

        assertThat(completedTasks).containsExactlyInAnyOrder("Do something",
                "Do something else",
                "Do a thing",
                "Do a thing",
                "Do yet another thing",
                "Do yet another thing",
                "Do another thing");
        assertThat(testSteps()).containsExactly(
                "Actor A",
                "Actor A does something",
                "Actor A does something with subtasks",
                "Actor A does a thing",
                "Actor A does yet another thing",
                "Actor B",
                "Actor B does something else",
                "Actor B does something with subtasks",
                "Actor B does a thing",
                "Actor B does yet another thing",
                "Actor B does another thing");
    }

    @Test
    public void nestedParallelTasksShouldBeRecordedEvenIfTheyTakeSomeTime() {

        Actor actorA = Actor.named("Actor A");
        Actor actorB = Actor.named("Actor B");

        InParallel.theActors(actorA, actorB).perform(
                () -> actorA.attemptsTo(doSomething(), doSomethingSlowly()),
                () -> actorB.attemptsTo(doSomethingElse(), doSomethingWithSubTasks(), doAnotherThing())
        );

        assertThat(completedTasks).contains(
                "Do something else",
                "Do something",
                "Do a thing",
                "Do yet another thing",
                "Do another thing",
                "Do something slowly");
        assertThat(testSteps()).containsExactly(
                "Actor A",
                "Actor A does something",
                "Actor A does something slowly",
                "Actor B",
                "Actor B does something else",
                "Actor B does something with subtasks",
                "Actor B does a thing",
                "Actor B does yet another thing",
                "Actor B does another thing");
    }

    private TestOutcome latestTestOutcome() {
        return StepEventBus.getParallelEventBus().getBaseStepListener().latestTestOutcome().get();
    }

    private List<String> testSteps() {
        return latestTestOutcome().getFlattenedTestSteps()
                .stream()
                .map(TestStep::getDescription)
                .collect(Collectors.toList());
    }
}
