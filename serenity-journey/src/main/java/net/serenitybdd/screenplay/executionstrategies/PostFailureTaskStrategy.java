package net.serenitybdd.screenplay.executionstrategies;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

/**
 * Created by john on 7/08/2015.
 */
public class PostFailureTaskStrategy implements TaskExecutionStrategy {

    private final Actor actor;

    public PostFailureTaskStrategy(Actor actor) {
        this.actor = actor;
    }

    @Override
    public void perform(Task todo) {
        // DO NOTHING

    }
}
