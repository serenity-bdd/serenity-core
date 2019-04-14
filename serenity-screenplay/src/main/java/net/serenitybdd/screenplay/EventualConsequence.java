package net.serenitybdd.screenplay;

import net.serenitybdd.core.environment.ConfiguredEnvironment;
import net.serenitybdd.core.time.Stopwatch;
import net.serenitybdd.markers.CanBeSilent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventualConsequence<T> implements Consequence<T>, CanBeSilent {
    public static final int A_SHORT_PERIOD_BETWEEN_TRIES = 100;
    private final Consequence<T> consequenceThatMightTakeSomeTime;
    private final long timeoutInMilliseconds;
    private final boolean isSilent;

    private AssertionError caughtAssertionError = null;
    private RuntimeException caughtRuntimeException = null;
    private List<Class<? extends Throwable>> exceptionsToIgnore = new ArrayList<>();

    public EventualConsequence(Consequence<T> consequenceThatMightTakeSomeTime, long timeoutInMilliseconds) {
        this(consequenceThatMightTakeSomeTime, timeoutInMilliseconds, false);
    }

    public EventualConsequence(Consequence<T> consequenceThatMightTakeSomeTime, long timeoutInMilliseconds, boolean isSilent) {
        this.consequenceThatMightTakeSomeTime = consequenceThatMightTakeSomeTime;
        this.timeoutInMilliseconds = timeoutInMilliseconds;
        this.isSilent = isSilent;
    }

    public EventualConsequence(Consequence<T> consequenceThatMightTakeSomeTime) {
        this(consequenceThatMightTakeSomeTime,
             ConfiguredEnvironment.getConfiguration().getElementTimeout() * 1000);
    }

    public static <T> EventualConsequence<T> eventually(Consequence<T> consequenceThatMightTakeSomeTime) {
        return new EventualConsequence(consequenceThatMightTakeSomeTime);
    }

    public EventualConsequenceBuilder<T> waitingForNoLongerThan(long amount) {
        return new EventualConsequenceBuilder<>(consequenceThatMightTakeSomeTime, amount);
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
                if (!shouldIgnoreException(assertionError)) {
                    caughtAssertionError = assertionError;
                }
            } catch (RuntimeException runtimeException) {
                if (!shouldIgnoreException(runtimeException)) {
                    caughtRuntimeException = runtimeException;
                }
            } catch (Throwable exception) {
                if (!shouldIgnoreException(exception)) {
                    throw exception;
                }
            }
            pauseBeforeNextAttempt();
        } while (stopwatch.lapTime() < timeoutInMilliseconds);

        throwAnyCaughtErrors();
    }

    private boolean shouldIgnoreException(Throwable exception) {
        return exceptionsToIgnore.contains(exception.getClass());
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

    public Consequence<T> ignoringExceptions(Class<? extends Throwable>... exceptionsToIgnore) {
        this.exceptionsToIgnore = Arrays.asList(exceptionsToIgnore);
        return this;
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

    @Override
    public boolean isSilent() {
        return isSilent;
    }

    public EventualConsequence<T>  withNoReporting() {
        return new EventualConsequence<T>(consequenceThatMightTakeSomeTime, timeoutInMilliseconds, true);
    }
}
