package net.serenitybdd.screenplay.executionstrategies;

import net.serenitybdd.screenplay.Task;

/**
 * Created by john on 7/08/2015.
 */
public interface TaskExecutionStrategy {
    void perform(Task todo);
}
