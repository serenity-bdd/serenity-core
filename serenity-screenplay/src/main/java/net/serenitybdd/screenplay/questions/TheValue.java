package net.serenitybdd.screenplay.questions;

import net.serenitybdd.screenplay.Question;

/**
 * Convenience class to convert an object into a Question
 */
public class TheValue {
    public static  <ANSWER> Question<ANSWER> of(ANSWER value) {
        return actor -> value;
    }

    public static  <ANSWER> Question<ANSWER> of(String subject, ANSWER value) {
        return new QuestionWithDefinedSubject<>(of(value), subject);
    }

}
