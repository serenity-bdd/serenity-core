package net.serenitybdd.screenplay;

public class EventualConsequenceBuilder<T> {
    private final Consequence<T> consequence;
    private final long amount;

    public <T> EventualConsequenceBuilder(Consequence consequence, long amount) {
        this.consequence = consequence;
        this.amount = amount;
    }

    public EventualConsequence<T> milliseconds() {
        return new EventualConsequence<T>(consequence, amount);
    }

    public EventualConsequence<T> seconds() {
        return new EventualConsequence<T>(consequence, amount * 1000);
    }
}
