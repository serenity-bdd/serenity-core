package net.serenitybdd.screenplay.questions;

import java.util.Set;

public interface AcceptsHints {
    void apply(Set<Class<? extends QuestionHint>> hints);
}
