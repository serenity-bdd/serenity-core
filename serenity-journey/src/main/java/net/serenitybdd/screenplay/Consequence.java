package net.serenitybdd.screenplay;

public interface Consequence<T> {
    void evaluateFor(Actor actor);
    Consequence<T> orComplainWith(Class<? extends AssertionError> complaintType);
}
