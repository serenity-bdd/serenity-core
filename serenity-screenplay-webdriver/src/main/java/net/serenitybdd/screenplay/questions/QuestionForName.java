package net.serenitybdd.screenplay.questions;

import net.serenitybdd.screenplay.Question;

public interface QuestionForName {
    Question<String> named(String name);
}
