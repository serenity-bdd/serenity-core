package net.serenitybdd.screenplay;

import net.serenitybdd.core.environment.ConfiguredEnvironment;
import net.serenitybdd.core.time.Stopwatch;

public class EventualConsequence<T> implements Consequence<T> {
    public static final int A_SHORT_PERIOD_BETWEEN_TRIES = 100;
    private final Consequence<T> consequenceThatMightTakeSomeTime;
    private final long timeout;

    private AssertionError caughtAssertionError = null;
    private RuntimeException caughtRuntimeException = null;

    public EventualConsequence(Consequence<T> consequenceThatMightTakeSomeTime, long timeout) {
        this.consequenceThatMightTakeSomeTime = consequenceThatMightTakeSomeTime;
        this.timeout = timeout;
    }

    public EventualConsequence(Consequence<T> consequenceThatMightTakeSomeTime) {
        this(consequenceThatMightTakeSomeTime,
             ConfiguredEnvironment.getConfiguration().getElementTimeout() * 1000);
    }

    public static <T> EventualConsequence<T> eventually(Consequence<T> consequenceThatMightTakeSomeTime) {
        return new EventualConsequence(consequenceThatMightTakeSomeTime);
    }

    public EventualConsequenceBuilder<T> waitingForNoLongerThan(long amount) {
        return new EventualConsequenceBuilder(consequenceThatMightTakeSomeTime, amount);
    }

    @Override
    public void evaluateFor(Actor actor) {
        Stopwatch stopwatch = new Stopwatch();

        stopwatch.start();
        do {
            try {
                consequenceThatMightTakeSomeTime.evaluateFor(actor);
                return;
            } catch (AssertionError assertionError) {
                caughtAssertionError = assertionError;
            } catch (RuntimeException runtimeException) {
                caughtRuntimeException = runtimeException;
            }
            pauseBeforeNextAttempt();
        } while (stopwatch.lapTime() < timeout);

        throwAnyCaughtErrors();
    }

    private void pauseBeforeNextAttempt() {
        try {
            Thread.sleep(A_SHORT_PERIOD_BETWEEN_TRIES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void throwAnyCaughtErrors() {
        if (caughtAssertionError != null) {
            throw caughtAssertionError;
        }
        if (caughtRuntimeException != null) {
            throw caughtRuntimeException;
        }
    }

    @Override
    public String toString() {
        return consequenceThatMightTakeSomeTime.toString();
    }

    @Override
    public Consequence<T> orComplainWith(Class<? extends Error> complaintType) {
        return new EventualConsequence(consequenceThatMightTakeSomeTime.orComplainWith(complaintType));
    }

    @Override
    public Consequence<T> orComplainWith(Class<? extends Error> complaintType, String complaintDetails) {
        return new EventualConsequence(consequenceThatMightTakeSomeTime.orComplainWith(complaintType, complaintDetails));
    }

    @Override
    public Consequence<T> whenAttemptingTo(Performable performable) {
        return new EventualConsequence<T>(consequenceThatMightTakeSomeTime.whenAttemptingTo(performable));
    }

    @Override
    public Consequence<T> because(String explanation) {
        return new EventualConsequence<T>(consequenceThatMightTakeSomeTime.because(explanation));
    }

}
