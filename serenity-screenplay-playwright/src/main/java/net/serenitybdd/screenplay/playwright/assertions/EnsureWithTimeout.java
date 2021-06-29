package net.serenitybdd.screenplay.playwright.assertions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;

import java.util.function.Consumer;

public class EnsureWithTimeout implements Performable {

    private final String description;
    private final Consumer<Actor> performableOperation;
    private final Double timeout;

    public static EnsureWithTimeout that(String description, Consumer<Actor> performableOperation) {
        return new EnsureWithTimeout(description, performableOperation, null);
    }

    protected EnsureWithTimeout(String description, Consumer<Actor> performableOperation, Double timeout) {
        this.performableOperation = performableOperation;
        this.description = description;
        this.timeout = timeout;
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        performableOperation.accept(actor);
    }
}
