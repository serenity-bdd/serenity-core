package net.thucydides.core.scheduling;

import java.util.concurrent.TimeUnit;

public class TimeoutSchedule<T> extends TimeSchedule<T> {

    public TimeoutSchedule(ThucydidesFluentWait<T> fluentWait, int amount) {
        super(fluentWait, amount);
    }

    @Override
    protected ThucydidesFluentWait<T> updateWaitBy(int amount, TimeUnit unit) {
        return fluentWait.withTimeout(amount, unit);
    }
}