package net.serenitybdd.screenplay.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

public class TheMemory implements Question<Boolean> {
    private final String memoryKey;

    public TheMemory(String memoryKey) {
        this.memoryKey = memoryKey;
    }

    @Override
    public Boolean answeredBy(Actor actor) {
        return actor.recall(memoryKey) != null;
    }

    public static class TheMemoryQuestionBuilder {

        private final String memoryKey;

        public TheMemoryQuestionBuilder(String memoryKey) {
            this.memoryKey = memoryKey;
        }

        public TheMemory isPresent() {
            return new TheMemory(memoryKey);
        }
    }

    public static TheMemoryQuestionBuilder withKey(String memoryKey) {
        return new TheMemoryQuestionBuilder(memoryKey);
    }
}
