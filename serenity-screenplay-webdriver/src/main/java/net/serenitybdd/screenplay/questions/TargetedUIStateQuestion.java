package net.serenitybdd.screenplay.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

class TargetedUIStateQuestion<T> implements Question<T> {

    private final TargetedUIState<T> targetedUIState;

    TargetedUIStateQuestion(TargetedUIState<T> targetedUIState) {
        this.targetedUIState = targetedUIState;
    }

    @Override
    public T answeredBy(Actor actor) {
        return targetedUIState.resolve();
    }
}
