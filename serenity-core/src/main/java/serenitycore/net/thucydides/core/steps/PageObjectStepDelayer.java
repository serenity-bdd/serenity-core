package serenitycore.net.thucydides.core.steps;

import serenitycore.net.serenitybdd.core.pages.PageObject;
import serenitymodel.net.serenitybdd.core.time.SystemClock;

public class PageObjectStepDelayer<T extends PageObject> {

    private final SystemClock clock;
    private final T parent;

    public PageObjectStepDelayer(SystemClock clock, T parent) {
        this.clock = clock;
        this.parent = parent;
    }

    public WaitForBuilder<T> waitFor(int duration) {
        return new WaitForBuilder<>(duration, parent, clock);
    }
}
