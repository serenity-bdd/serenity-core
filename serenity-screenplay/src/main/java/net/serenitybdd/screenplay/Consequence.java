package net.serenitybdd.screenplay;

public interface Consequence<T> {
    void evaluateFor(Actor actor);
    Consequence<T> orComplainWith(Class<? extends Error> complaintType);
    Consequence<T> orComplainWith(Class<? extends Error> complaintType, String complaintDetails);
}
