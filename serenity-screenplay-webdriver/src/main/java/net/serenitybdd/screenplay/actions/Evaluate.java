package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.steps.Instrumented;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.webtests.actions.LastScriptExecution;
import net.thucydides.core.annotations.Step;

/**
 * Executes JavaScript in the context of the currently selected frame or window. The script fragment provided will be executed as the body of an anonymous function.
 * Within the script, use document to refer to the current document. Note that local variables will not be available once the script has finished executing, though global variables will persist.
 * If the script has a return value (i.e. if the script contains a return statement), you can retrieve the value using the result() method:
 * For an HTML element, this method returns a WebElement
 * For a decimal, a Double is returned
 * For a non-decimal number, a Long is returned
 * For a boolean, a Boolean is returned
 * For all other cases, a String is returned.
 * For an array, return a List<Object> with each object following the rules above. We support nested lists.
 * For a map, return a Map<String, Object> with values following the rules above.
 * Unless the value is null or there is no return value, in which null is returned
 * Arguments must be a number, a boolean, a String, WebElement, or a List of any combination of the above. An exception will be thrown if the arguments do not meet these criteria. The arguments will be made available to the JavaScript via the "arguments" magic variable, as if the function were called via "Function.apply"
 */
public class Evaluate implements Interaction {

    private final String expression;
    private Object[] parameters;
    private Object lastResult;

    public Evaluate(String expression) {
        this.expression = expression;
    }

    public Evaluate withParameters(Object... parameters) {
        this.parameters = parameters;
        return this;
    }

    @Step("Execute JavaScript #expression")
    public <T extends Actor> void performAs(T actor) {
        lastResult = BrowseTheWeb.as(actor).evaluateJavascript(expression, parameters);
        if (lastResult != null) {
            actor.remember(LastScriptExecution.LAST_SCRIPT_EXECUTION_RESULT, lastResult);
        } else {
            actor.forget(LastScriptExecution.LAST_SCRIPT_EXECUTION_RESULT);
        }

    }

    public static Evaluate javascript(String expression, Object... parameters) {
        Evaluate evaluate = Instrumented.instanceOf(Evaluate.class).withProperties(expression);
        return evaluate.withParameters(parameters);
    }

    public Question<Object> result() {
        return new JavascriptAnswer(this);
    }

    public static class JavascriptAnswer implements Question<Object> {
        private final Evaluate expression;

        public JavascriptAnswer(Evaluate expression) {
            this.expression = expression;
        }

        @Override
        public Object answeredBy(Actor actor) {
            actor.attemptsTo(expression);
            return expression.lastResult;
        }
    }
}
