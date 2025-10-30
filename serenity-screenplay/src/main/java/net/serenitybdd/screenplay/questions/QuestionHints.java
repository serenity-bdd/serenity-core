package net.serenitybdd.screenplay.questions;

import net.serenitybdd.screenplay.Question;
import org.hamcrest.Matcher;

import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

public class QuestionHints {
    public static Set<Class<? extends QuestionHint>> fromAssertion(Matcher matcher) {
        return stream(matcher.getClass().getInterfaces())
                .filter(QuestionHint.class::isAssignableFrom)
                .map( matcherInterface -> ((Class<? extends QuestionHint>) matcherInterface) )
                .collect(Collectors.toSet());
    }

    public static HintAdder addHints(Set<Class<? extends QuestionHint>> hints) {
        return new HintAdder(hints);
    }

    public static class HintAdder {
        private final Set<Class<? extends QuestionHint>> hints;

        public HintAdder(Set<Class<? extends QuestionHint>> hints) {
            this.hints = hints;
        }

        public <T> void to(Question<T> question) {
            if (question instanceof AcceptsHints) {
                ((AcceptsHints) question).apply(hints);
            }
        }
    }
}
