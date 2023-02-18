package net.thucydides.core.scheduling;

import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.webdriver.TemporalUnitConverter;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.ui.Sleeper;
import org.openqa.selenium.support.ui.Wait;

import java.time.Clock;
import java.time.Duration;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class ThucydidesFluentWait<T> implements Wait<T> {

    protected Duration timeout =  Duration.ofMillis(500);;
    protected Duration interval = Duration.ofMillis(50);

    private List<Class<? extends RuntimeException>> ignoredExceptions = new LinkedList<>();

    private final Clock clock;
    private final T input;
    private final Sleeper sleeper;
    private Supplier<String> messageSupplier = () -> null;

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

    @Override
    public <V> V until(Function<? super T, V> isTrue) {
        long end = getClock().millis() +  timeout.toMillis();
        RuntimeException lastException = null;
        String waitForConditionMessage = isTrue.toString();
        while (true) {
            if (webdriverCallsAreSuspended()) {
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

            if (!(getClock().millis() < end)){
                String message = messageSupplier != null ?
                        messageSupplier.get() : null;

                String timeoutMessage = String.format(
                        "Expected condition failed: %s (tried for %d second(s) with %d milliseconds interval)",
                        message == null ? "waiting for " + isTrue : message,
                        timeout.getSeconds(), interval.toMillis());
                throw timeoutException(timeoutMessage, lastException);
            }

            try {
                doWait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new WebDriverException(e);
            }
        }
    }

    private boolean webdriverCallsAreSuspended() {
        return StepEventBus.getParallelEventBus().webdriverCallsAreSuspended();
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
        this.timeout = Duration.of(duration, TemporalUnitConverter.fromTimeUnit(unit));
        return this;
    }

    public ThucydidesFluentWait<T> withTimeout(Duration timeout) {
        this.timeout = timeout;
        return this;
    }

    /**
     * Sets the message to be displayed when time expires.
     *
     * @param message to be appended to default.
     * @return A self reference.
     */
    public ThucydidesFluentWait<T> withMessage(final String message) {
        this.messageSupplier = () -> message;
        return this;
    }

    /**
     * Sets the message to be evaluated and displayed when time expires.
     *
     * @param messageSupplier to be evaluated on failure and appended to default.
     * @return A self reference.
     */
    public ThucydidesFluentWait<T> withMessage(Supplier<String> messageSupplier) {
        this.messageSupplier = messageSupplier;
        return this;
    }

    public ThucydidesFluentWait<T> pollingEvery(long duration, TimeUnit unit) {
        this.interval = Duration.of(duration, TemporalUnitConverter.fromTimeUnit(unit));
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
