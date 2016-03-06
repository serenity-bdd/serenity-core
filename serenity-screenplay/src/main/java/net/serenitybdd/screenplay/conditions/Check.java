package net.serenitybdd.screenplay.conditions;

import net.serenitybdd.screenplay.Question;

public class Check {
    public static ConditionalPerformable whether(Boolean condition) {
        return new ConditionalPerformableOnBoolean(condition);
    }

    public static ConditionalPerformable whether(Question<Boolean> condition) {
        return new ConditionalPerformableOnQuestion(condition);
    }

}
