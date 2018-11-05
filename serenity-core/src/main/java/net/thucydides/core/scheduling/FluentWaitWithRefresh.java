package net.thucydides.core.scheduling;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Sleeper;

import java.time.Clock;

public class FluentWaitWithRefresh<T> extends ThucydidesFluentWait<T> {

    public FluentWaitWithRefresh(T input, Clock clock, Sleeper sleeper) {
        super(input, clock, sleeper);
    }

    @Override
    public void doWait() throws InterruptedException {
        getSleeper().sleep(interval);
        ((WebDriver) getInput()).navigate().refresh();
    }
}