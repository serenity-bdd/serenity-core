package net.serenitybdd.core.configurers;

import net.thucydides.core.steps.StepEventBus;

public class WebDriverConfigurer {
    public void reenableDrivers() {
        StepEventBus.getParallelEventBus().reenableWebDriver();
    }
}
