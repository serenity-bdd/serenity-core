package net.serenitybdd.assertions;

import jnr.ffi.annotations.In;
import net.serenitybdd.core.java8.TriConsumer;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Question;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SerenityRunner.class)
public class WhenUsingScreenplayMatchersWeCan {

    Actor collette = Actor.named("Collette");

    @Test
    public void makeSimpleScreenplayAssertionsInAnIntuitiveWay() {

        int age = 20;

        assertThat(age).isEqualTo(20);

        Question<Integer> herAge = actor -> 20;

        collette.attemptsTo(
                Ensure.that(age).isGreaterThan(30),
                Ensure.that(herAge).isGreaterThan(0)
        );
    }

    private static class Ensure {
        public static IntegerEnsurer that(Integer age) {
            return new IntegerEnsurer(age);
        }
        public static IntegerQuestionEnsurer that(Question<Integer> age) {
            return new IntegerQuestionEnsurer(age);
        }
    }

    private static class PerformablePredicate<A,E> implements Performable {

        private A actualValue;
        private E expectedValue;
        private final BiConsumer<A,E> predicate;

        private PerformablePredicate(A actualValue, BiConsumer<A,E> predicate, E expectedValue) {
            this.predicate = predicate;
            this.actualValue = actualValue;
            this.expectedValue = expectedValue;
        }

        @Override
        public <T extends Actor> void performAs(T actor) {
            predicate.accept(actualValue, expectedValue);
        }
    }


    private static class PerformableActorPredicate<Q extends Question<E>,E> implements Performable {

        private Q actualValue;
        private E expectedValue;
        private final TriConsumer<Actor,E,E> predicate;

        private PerformableActorPredicate(Q actualValue, TriConsumer<Actor,E,E> predicate, E expectedValue) {
            this.predicate = predicate;
            this.actualValue = actualValue;
            this.expectedValue = expectedValue;
        }

        @Override
        public <T extends Actor> void performAs(T actor) {
            predicate.accept(actor, actualValue.answeredBy(actor), expectedValue);
        }
    }

    private static class IntegerQuestionEnsurer {

        private static TriConsumer<Actor, Question<Integer>, Integer> EQUALS = (actor, actual, expected) -> assertThat(actual.answeredBy(actor)).isEqualTo(expected);
        private static TriConsumer<Actor, Question<Integer>, Integer> GREATER_THAN = (actor, actual, expected) -> assertThat(actual.answeredBy(actor)).isGreaterThan(expected);
        private static TriConsumer<Actor, Question<Integer>, Integer> LESS_THAN = (actor, actual, expected) -> assertThat(actual.answeredBy(actor)).isLessThan(expected);

        private Question<Integer> actualValue;

        public IntegerQuestionEnsurer(Question<Integer> actualValue) {
            this.actualValue = actualValue;
        }

        public Performable isEqualTo(Integer expectedValue) {
            return new PerformableActorPredicate(actualValue, EQUALS, expectedValue);
        }

        public Performable isGreaterThan(Integer expectedValue) {
            return new PerformableActorPredicate(actualValue, GREATER_THAN, expectedValue);
        }

        public Performable isLessThanThan(int expectedValue) {
            return new PerformableActorPredicate(actualValue, LESS_THAN, expectedValue);
        }
    }

    private static class IntegerEnsurer {

        private static BiConsumer<Integer,Integer> EQUALS = (actual, expected) -> assertThat(actual).isEqualTo(expected);
        private static BiConsumer<Integer,Integer> GREATER_THAN = (actual, expected) -> assertThat(actual).isGreaterThan(expected);
        private static BiConsumer<Integer,Integer> LESS_THAN = (actual, expected) -> assertThat(actual).isLessThan(expected);

        private int actualValue;

        public IntegerEnsurer(Integer actualValue) {
            this.actualValue = actualValue;
        }

        public Performable isEqualTo(Integer expectedValue) {
            return new PerformablePredicate(actualValue, EQUALS, expectedValue);
        }

        public Performable isGreaterThan(Integer expectedValue) {
            return new PerformablePredicate(actualValue, GREATER_THAN, expectedValue);
        }

        public Performable isLessThanThan(Integer expectedValue) {
            return new PerformablePredicate(actualValue, LESS_THAN, expectedValue);
        }
    }
}
