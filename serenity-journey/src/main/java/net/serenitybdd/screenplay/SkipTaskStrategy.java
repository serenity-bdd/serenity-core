package net.serenitybdd.screenplay;

import net.serenitybdd.screenplay.executionstrategies.TaskExecutionStrategy;

/**
 * Created by john on 7/08/2015.
 */
public class SkipTaskStrategy implements TaskExecutionStrategy {

    private final Actor actor;

    public SkipTaskStrategy(Actor actor) {
        this.actor = actor;
    }

    @Override
    public void perform(Task todo) {
        // DO NOTHING
    }
}
