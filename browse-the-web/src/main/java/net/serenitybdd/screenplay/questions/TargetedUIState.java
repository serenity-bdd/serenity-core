package net.serenitybdd.screenplay.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.targets.Target;

import java.util.List;

public abstract class TargetedUIState<T> extends UIState<T>{

    protected final Target target;

    protected TargetedUIState(Target target, Actor actor) {
        super(actor);
        this.target = target;
    }

    public abstract List<T> resolveAll();

    public List<T> asList() {
        return resolveAll();
    }

    public <T> List<T> asListOf(Class<T> type) {
        return convertToEnums(type, asList());
    }

}
