package net.serenitybdd.screenplay.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.BaseConsequence;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.QuestionSubject;

public class ConsequenceGroup<T> extends BaseConsequence<T> {

    private final Question<?> questionGroup;
    private final String subject;

    public <T> ConsequenceGroup(Question<?> questionGroup) {
        this.questionGroup = questionGroup;
        this.subject = QuestionSubject.fromClass(questionGroup.getClass()).andQuestion(questionGroup).subject();
    }

    @Override
    public void evaluateFor(Actor actor) {
        performSetupActionsAs(actor);
        questionGroup.answeredBy(actor);
    }

    @Override
    public String toString() {
        String template = explanation.orElse("Then %s");
        return addRecordedInputValuesTo(String.format(template, subjectText.orElse(subject)));
    }

}
