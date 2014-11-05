package net.thucydides.core.scheduling;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import net.thucydides.core.steps.StepEventBus;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.ui.Clock;
import org.openqa.selenium.support.ui.Duration;
import org.openqa.selenium.support.ui.Sleeper;
import org.openqa.selenium.support.ui.Wait;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

public abstract class ThucydidesFluentWait<T> implements Wait<T> {

    public static Duration FIVE_HUNDRED_MILLIS = new Duration(500, MILLISECONDS);

    protected Duration timeout = FIVE_HUNDRED_MILLIS;
    protected Duration interval = FIVE_HUNDRED_MILLIS;

    private List<Class<? extends RuntimeException>> ignoredExceptions = Lists.newLinkedList();

    private final Clock clock;
    private final T input;
    private final Sleeper sleeper;

    public ThucydidesFluentWait(T input, Clock clock, Sleeper sleeper) {
        this.input = checkNotNull(input);
        this.clock = checkNotNull(clock);
        this.sleeper = checkNotNull(sleeper);
    }

    protected Clock getClock() {
        return clock;
    }

    protected T getInput() {
        return input;
    }

    protected Sleeper getSleeper() {
        return sleeper;
    }

    public <V> V until(Function<? super T, V> isTrue) {
        long end = getClock().laterBy(timeout.in(MILLISECONDS));
        RuntimeException lastException = null;
        while (true) {
            if (aPreviousStepHasFailed()) {
                return (V) Boolean.TRUE;
            }
            try {
                V value = isTrue.apply(input);
                if (value != null && Boolean.class.equals(value.getClass())) {
                    if (Boolean.TRUE.equals(value)) {
                        return value;
                    }
                }
                else {
                    throw new IllegalArgumentException("Condition should be a boolean function");
                }
            } catch (RuntimeException e) {
                lastException = propagateIfNotIngored(e);
            }

            if (!getClock().isNowBefore(end)) {
                throw timeoutException(String.format("Timed out after %d seconds",
                        timeout.in(SECONDS)), lastException);
            }

            try {
                doWait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new WebDriverException(e);
            }
        }
    }

    private boolean aPreviousStepHasFailed() {
        return StepEventBus.getEventBus().aStepInTheCurrentTestHasFailed();
    }

    public abstract void doWait() throws InterruptedException;

    private RuntimeException propagateIfNotIngored(RuntimeException e) {
        for (Class<? extends RuntimeException> ignoredException : ignoredExceptions) {
            if (ignoredException.isInstance(e)) {
                return e;
            }
        }
        throw e;
    }

    public ThucydidesFluentWait<T> ignoring(Class<? extends RuntimeException>... types) {
        ignoredExceptions.addAll(Arrays.asList(types));
        return this;
    }

    public ThucydidesFluentWait<T> withTimeout(long duration, TimeUnit unit) {
        this.timeout = new Duration(duration, unit);
        return this;
    }

    public ThucydidesFluentWait<T> pollingEvery(long duration, TimeUnit unit) {
        this.interval = new Duration(duration, unit);
        return this;
    }

    protected RuntimeException timeoutException(String message, RuntimeException lastException) {
        throw new TimeoutException(message, lastException);
    }

    public TimeoutSchedule withTimeoutOf(int amount) {
        return new TimeoutSchedule(this, amount);
    }

    public PollingSchedule pollingEvery(int amount) {
        return new PollingSchedule(this, amount);
    }
}