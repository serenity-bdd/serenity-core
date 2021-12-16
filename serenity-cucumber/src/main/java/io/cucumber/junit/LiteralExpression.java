package io.cucumber.junit;

import io.cucumber.tagexpressions.Expression;

import java.util.List;

public class LiteralExpression implements Expression {

    private final String value;

    LiteralExpression(String value) {
        this.value = value;
    }

    public boolean evaluate(List<String> variables) {
        return variables.contains(this.value);
    }

    public String toString() {
        return this.value.replaceAll("\\(", "\\\\(").replaceAll("\\)", "\\\\)");
    }
}
