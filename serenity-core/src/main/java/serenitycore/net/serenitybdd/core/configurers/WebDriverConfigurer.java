package serenitycore.net.serenitybdd.core.configurers;

import serenitycore.net.thucydides.core.steps.StepEventBus;

public class WebDriverConfigurer {
    public void reenableDrivers() {
        StepEventBus.getEventBus().reenableWebDriver();
    }
}
