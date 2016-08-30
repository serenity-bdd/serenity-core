package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.steps.Instrumented;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.thucydides.core.annotations.Step;

public class Evaluate implements Interaction {

    private final String expression;
    private Object[] parameters;

    public Evaluate(String expression) {
        this.expression = expression;
    }

    public Evaluate withParameters(Object... parameters) {
        this.parameters = parameters;
        return this;
    }

    @Step("Execute JavaScript #expression")
    public <T extends Actor> void performAs(T theUser) {
        BrowseTheWeb.as(theUser).evaluateJavascript(expression, parameters);
    }

    public static Evaluate javascript(String expression, Object... parameters) {
        Evaluate evaluate = Instrumented.instanceOf(Evaluate.class).withProperties(expression);
        return evaluate.withParameters(parameters);
    }


}
