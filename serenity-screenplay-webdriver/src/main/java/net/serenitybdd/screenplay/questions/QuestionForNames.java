package net.serenitybdd.screenplay.questions;

import net.serenitybdd.screenplay.Question;

import java.util.Collection;

public interface QuestionForNames {
    Question<Collection<String>> named(String name);
}
