package serenityscreenplay.net.serenitybdd.screenplay.questions;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.BaseConsequence;
import serenityscreenplay.net.serenitybdd.screenplay.Question;
import serenityscreenplay.net.serenitybdd.screenplay.QuestionSubject;

public class ConsequenceGroup<T> extends BaseConsequence<T> {

    private final Question<?> questionGroup;
    private final String subject;

    public <T> ConsequenceGroup(Question<?> questionGroup) {
        this.questionGroup = questionGroup;
        this.subject = QuestionSubject.fromClass(questionGroup.getClass()).andQuestion(questionGroup).subject();
    }

    @Override
    public void evaluateFor(Actor actor) {
        questionGroup.answeredBy(actor);
//        try {
//            String groupTitle = "consequence group";
//
//            StepEventBus.getEventBus().stepStarted(ExecutedStepDescription.withTitle(groupTitle));
//
//            questionGroup.answeredBy(actor);
//
//        } catch (Throwable error) {
//            throw error;
//        } finally {
//            StepEventBus.getEventBus().stepFinished();
//        }

    }

    @Override
    public String toString() {
        String template = explanation.orElse("Then %s");
        return addRecordedInputValuesTo(String.format(template, subjectText.orElse(subject)));
    }

}
