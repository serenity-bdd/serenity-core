package net.serenitybdd.screenplay.questions;

import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;

import java.util.function.Function;

public class WebDriverQuestion {

    public static UIInteractionQuestionBuilder about(String subject) {
        return new UIInteractionQuestionBuilder(subject);
    }

    public static class UIInteractionQuestionBuilder {
        private final String subject;

        UIInteractionQuestionBuilder(String subject) {
            this.subject = subject;
        }

        public <T> Question<T> answeredBy(Function<BrowseTheWeb, T> questionToAsk) {
            Question<T> uiInteractionQuestion = actor -> questionToAsk.apply(BrowseTheWeb.as(actor));
            return new QuestionWithDefinedSubject(uiInteractionQuestion, subject);
        }
    }
}