package net.thucydides.core.scheduling;

import org.openqa.selenium.support.ui.Sleeper;

import java.time.Clock;

public class NormalFluentWait<T> extends ThucydidesFluentWait<T> {

    public NormalFluentWait(T input) {
        super(input, Clock.systemDefaultZone(), Sleeper.SYSTEM_SLEEPER);
    }

    public NormalFluentWait(T input, Clock clock, Sleeper sleeper) {
        super(input, clock, sleeper);
    }

    @Override
    public void doWait() throws InterruptedException {
        getSleeper().sleep(interval);
    }
}