package net.serenitybdd.screenplay;

import net.serenitybdd.core.environment.ConfiguredEnvironment;
import net.serenitybdd.core.time.Stopwatch;
import net.serenitybdd.markers.CanBeSilent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static net.serenitybdd.screenplay.Actor.ErrorHandlingMode.IGNORE_EXCEPTIONS;

public class EventualConsequence<T> implements Consequence<T>, CanBeSilent {
    public static final int A_SHORT_PERIOD_BETWEEN_TRIES = 100;
    private final Consequence<T> consequenceThatMightTakeSomeTime;
    private final long timeoutInMilliseconds;
    private final boolean isSilent;

    private Class<? extends Error> complaintType;
    private String complaintDetails;

    private AssertionError caughtAssertionError = null;
    private RuntimeException caughtRuntimeException = null;
    private List<Class<? extends Throwable>> exceptionsToIgnore = new ArrayList<>();
    private List<Performable> setupActions = new ArrayList<>();

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
             ConfiguredEnvironment.getConfiguration().getElementTimeoutInSeconds() * 1000);
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
                performSetupActionsAs(actor);
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
            throwComplaintTypeErrorIfSpecified(caughtAssertionError);
            throw caughtAssertionError;
        }
        if (caughtRuntimeException != null) {
            throwComplaintTypeErrorIfSpecified(caughtRuntimeException);
            throw caughtRuntimeException;
        }
    }

    @Override
    public String toString() {
        return consequenceThatMightTakeSomeTime.toString();
    }

    @Override
    public Consequence<T> orComplainWith(Class<? extends Error> complaintType) {
        this.complaintType = complaintType;
        return this;
    }

    public Consequence<T> ignoringExceptions(Class<? extends Throwable>... exceptionsToIgnore) {
        this.exceptionsToIgnore = Arrays.asList(exceptionsToIgnore);
        return this;
    }

    @Override
    public Consequence<T> orComplainWith(Class<? extends Error> complaintType, String complaintDetails) {
        this.complaintType = complaintType;
        this.complaintDetails = complaintDetails;
        return this;
    }

    @Override
    public Consequence<T> whenAttemptingTo(Performable performable) {
        return new EventualConsequence<T>(consequenceThatMightTakeSomeTime.whenAttemptingTo(performable),
            timeoutInMilliseconds, isSilent);
    }

    @Override
    public Consequence<T> because(String explanation) {
        return new EventualConsequence<T>(consequenceThatMightTakeSomeTime.because(explanation),
            timeoutInMilliseconds, isSilent);
    }

    @Override
    public boolean isSilent() {
        return isSilent;
    }

    public EventualConsequence<T>  withNoReporting() {
        return new EventualConsequence<T>(consequenceThatMightTakeSomeTime, timeoutInMilliseconds, true);
    }

    private void throwComplaintTypeErrorIfSpecified(Throwable actualError) {
        if (complaintType != null) {
            throw Complaint.from(complaintType, complaintDetails, actualError);
        }
    }


    public Consequence<T> after(Performable... actions) {
        this.setupActions.addAll(asList(actions));
        return this;
    }

    protected void performSetupActionsAs(Actor actor) {
        actor.attemptsTo(
                IGNORE_EXCEPTIONS,
                setupActions.toArray(new Performable[]{})
        );
    }
}
