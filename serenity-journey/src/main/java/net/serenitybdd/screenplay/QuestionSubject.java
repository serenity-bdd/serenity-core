package net.serenitybdd.screenplay;

import net.thucydides.core.util.NameConverter;

public class QuestionSubject {
    public static String fromClass(Class<? extends Question> questionClass) {
        return NameConverter.humanize(questionClass.getSimpleName()).toLowerCase();
    }
}
