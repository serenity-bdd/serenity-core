package net.serenitybdd.screenplay.questions;

import net.serenitybdd.screenplay.Question;

import java.util.List;

public interface QuestionForNames {
    Question<List<String>> named(String name);
}
