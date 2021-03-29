package serenityscreenplay.net.serenitybdd.screenplay.conditions;

import serenityscreenplay.net.serenitybdd.screenplay.Question;
import org.hamcrest.Matcher;

public class Check {
    public static ConditionalPerformable whether(Boolean condition) {
        return new ConditionalPerformableOnBoolean(condition);
    }

    public static ConditionalPerformable whether(Question<Boolean> condition) {
        return new ConditionalPerformableOnQuestion(condition);
    }

    public static <T> ConditionalPerformable whether(Question<T> question, Matcher<T> matcher) {
        Question<Boolean> condition = actor -> matcher.matches(question.answeredBy(actor));

        return new ConditionalPerformableOnQuestion(condition);
    }
}
