package net.serenitybdd.screenplay.webtests.actions;

import net.serenitybdd.screenplay.Question;

/**
 * Retrieve the result of the last Javascript execution.
 */
public class LastScriptExecution {
    public static final String LAST_SCRIPT_EXECUTION_RESULT = "LAST_SCRIPT_EXECUTION_RESULT";

    public static Question<Object> result() {
        return actor -> actor.recall("LAST_SCRIPT_EXECUTION_RESULT");
    }
}
