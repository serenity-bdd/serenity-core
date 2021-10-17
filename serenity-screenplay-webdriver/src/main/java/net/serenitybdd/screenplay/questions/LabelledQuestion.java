package net.serenitybdd.screenplay.questions;

import net.serenitybdd.screenplay.Question;

import java.util.List;

public class LabelledQuestion {
    public static Question<Boolean> answer(String label, Question<Boolean> questionToAsk) {
        return Question.about(label).answeredBy(questionToAsk);
    }

    public static Question<List<Boolean>> answerEach(String label, Question<List<Boolean>> questionToAsk) {
        return Question.about(label).answeredBy(questionToAsk);
    }
}
