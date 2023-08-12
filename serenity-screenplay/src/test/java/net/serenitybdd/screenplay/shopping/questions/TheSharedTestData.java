package net.serenitybdd.screenplay.shopping.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.shopping.tasks.TestData;
import net.serenitybdd.annotations.Shared;

public class TheSharedTestData {

    public static Question<Integer> currentCounter() {
        return new CounterQuestion();
    }

    public static Question<Boolean> isInitalised() {
        return new InitialisedQuestion();
    }

    public static class CounterQuestion implements Question<Integer> {
        @Shared
        TestData testData;

        @Override
        public Integer answeredBy(Actor actor) {
            return testData.counter;
        }
    }

    public static class InitialisedQuestion implements Question<Boolean> {
        @Shared
        TestData testData;

        @Override
        public Boolean answeredBy(Actor actor) {
            return testData.initialised;
        }
    }

}
