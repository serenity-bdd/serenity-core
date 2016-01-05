package net.serenitybdd.screenplay.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;

public class JavaScript extends UIState<String> {

    private final String expression;
    private final Object[] parameters;

    public JavaScript(String expression, Object[] parameters, Actor actor) {
        super(actor);
        this.expression = expression;
        this.parameters = parameters;
    }

    public static JavaScriptBuilder evaluate(String expression) {
        return new JavaScriptBuilder(expression);
    }

    @Override
    public String resolve() {
        return BrowseTheWeb.as(actor).evaluateJavascript(expression, parameters).toString();
    }

    public static final class JavaScriptBuilder {
        private final String expression;
        private Object[] parameters = new Object[]{};

        public JavaScriptBuilder(String expression, Object... parameters) {
            this.expression = expression;
            this.parameters = parameters;
        }

        public JavaScriptBuilder withParameters(Object... parameters) {
            this.parameters = parameters;
            return this;
        }

        public JavaScript viewedBy(Actor actor) {
            return new JavaScript(expression, parameters, actor);
        }

    }

}
