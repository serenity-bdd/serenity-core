package net.serenitybdd.screenplay;

public interface Consequence<T> {
    void evaluateFor(Actor actor);
    Consequence<T> orComplainWith(Class<? extends Throwable> complaintType);
    Consequence<T> orComplainWith(Class<? extends Throwable> complaintType, String complaintDetails);
    Consequence<T> whenAttemptingTo(Performable performable);
    Consequence<T> because(String explanation);
}
