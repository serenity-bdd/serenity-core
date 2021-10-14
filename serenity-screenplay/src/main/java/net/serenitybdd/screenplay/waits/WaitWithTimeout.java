package net.serenitybdd.screenplay.waits;

import net.serenitybdd.screenplay.SilentInteraction;

import java.time.Duration;

public abstract class WaitWithTimeout extends SilentInteraction {
    protected Duration timeout;

    public TimeoutBuilder forNoMoreThan(long timeout) {
        return new TimeoutBuilder(timeout, this);
    }

    public <T extends WaitWithTimeout> T forNoMoreThan(Duration timeout) {
        this.timeout = timeout;
        return (T) this;
    }

    public static class TimeoutBuilder implements WithTimeUnits {
        private final WaitWithTimeout waitWithTimeout;
        private final long duration;

        public TimeoutBuilder(long duration, WaitWithTimeout waitWithTimeout) {
            this.waitWithTimeout = waitWithTimeout;
            this.duration = duration;
        }

        @Override
        public WaitWithTimeout seconds() {
            waitWithTimeout.timeout = Duration.ofSeconds(duration);
            return waitWithTimeout;
        }

        @Override
        public WaitWithTimeout milliseconds() {
            waitWithTimeout.timeout = Duration.ofMillis(duration);
            return waitWithTimeout;
        }

        public WaitWithTimeout minutes() {
            waitWithTimeout.timeout = Duration.ofMinutes(duration);
            return waitWithTimeout;
        }
    }
}
