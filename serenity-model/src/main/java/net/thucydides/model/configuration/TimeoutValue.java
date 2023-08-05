package net.thucydides.model.configuration;

import java.util.concurrent.TimeUnit;

public class TimeoutValue {
    private final long timeout;
    private final TimeUnit unit;

    public TimeoutValue(long timeout, TimeUnit unit) {
        this.timeout = timeout;
        this.unit = unit;
    }

    public long getTimeout() {
        return timeout;
    }

    public TimeUnit getUnit() {
        return unit;
    }

}
