package serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions;

import serenitycore.net.serenitybdd.core.steps.Instrumented;
import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Interaction;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import serenitymodel.net.thucydides.core.annotations.Step;

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
