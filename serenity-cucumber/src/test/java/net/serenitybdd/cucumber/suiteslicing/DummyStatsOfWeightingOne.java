package net.serenitybdd.cucumber.suiteslicing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static java.math.BigDecimal.ONE;

public class DummyStatsOfWeightingOne implements TestStatistics {

    @Override
    public BigDecimal scenarioWeightFor(String feature, String scenario) {
        return ONE;
    }

    @Override
    public List<TestScenarioResult> records() {
        return new ArrayList<>();
    }
}
