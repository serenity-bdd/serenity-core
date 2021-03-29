package serenityscreenplay.net.serenitybdd.screenplay.shopping.questions;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Question;
import serenityscreenplay.net.serenitybdd.screenplay.shopping.tasks.TestData;
import serenitycore.net.thucydides.core.annotations.Shared;

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
