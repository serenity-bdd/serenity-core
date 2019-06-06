package net.thucydides.core.scheduling.fluent;

import net.thucydides.core.scheduling.SerenityFluentWait;

import java.util.concurrent.TimeUnit;

public class PollingSchedule<T> extends TimeSchedule<T> {

    public PollingSchedule(SerenityFluentWait fluentWait, int amount) {
        super(fluentWait, amount);
    }

    @Override
    protected SerenityFluentWait updateWaitBy(int amount, TimeUnit unit) {
        return (SerenityFluentWait) fluentWait.pollingEvery(amount, unit);
    }

}