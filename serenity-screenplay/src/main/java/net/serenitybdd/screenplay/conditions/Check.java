package net.serenitybdd.screenplay.conditions;

import net.serenitybdd.screenplay.Question;
import org.hamcrest.Matcher;

public class Check {
    public static ConditionalPerformable whether(Boolean condition) {
        return new ConditionalPerformableOnBoolean(condition);
    }

    public static ConditionalPerformable whether(Question<Boolean> condition) {
        return new ConditionalPerformableOnQuestion(condition);
    }

    public static <T> ConditionalPerformable whether(Question<? extends T> question, Matcher<T> matcher) {
        Question<Boolean> condition = actor -> matcher.matches(question.answeredBy(actor));

        return new ConditionalPerformableOnQuestion(condition);
    }

    public static <T> ConditionalPerformable whetherThe(Target target, Matcher<WebElementState> expectedState) {
        return new ConditionalPerformableOnTargetState(target, expectedState);
    }
}
