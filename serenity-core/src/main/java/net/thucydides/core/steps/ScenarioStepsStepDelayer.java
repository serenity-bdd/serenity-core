package net.thucydides.core.steps;

import net.serenitybdd.model.time.SystemClock;

public class ScenarioStepsStepDelayer<T extends ScenarioSteps> {

    private final SystemClock clock;
    private final T parent;

    public ScenarioStepsStepDelayer(SystemClock clock, T parent) {
        this.clock = clock;
        this.parent = parent;
    }

    public WaitForBuilder<T> waitFor(int duration) {
        return new WaitForBuilder(duration, parent, clock);
    }
}
