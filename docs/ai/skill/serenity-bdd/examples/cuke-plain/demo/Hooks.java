package demo;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import net.serenitybdd.core.Serenity;

public class Hooks {

    // Runs before every scenario. The Scenario parameter is optional.
    @Before(order = 0)
    public void beforeEachScenario(Scenario scenario) {
        Serenity.recordReportData().withTitle("Scenario tags").andContents(String.join(", ", scenario.getSourceTagNames()));
    }

    // Only for scenarios tagged @basket
    @Before(value = "@basket", order = 1)
    public void beforeBasketScenarios() {
        Serenity.recordReportData().withTitle("Setup").andContents("basket scenario");
    }

    @After
    public void afterEachScenario(Scenario scenario) {
        if (scenario.isFailed()) {
            Serenity.recordReportData().withTitle("Failed scenario").andContents(scenario.getName());
        }
    }
}
