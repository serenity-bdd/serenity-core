package net.thucydides.core.scheduling;

import java.util.concurrent.TimeUnit;

public class PollingSchedule<T> extends TimeSchedule<T> {

    public PollingSchedule(ThucydidesFluentWait<T> fluentWait, int amount) {
        super(fluentWait, amount);
    }

    @Override
    protected ThucydidesFluentWait<T> updateWaitBy(int amount, TimeUnit unit) {
        return fluentWait.pollingEvery(amount, unit);
    }

}