package net.serenitybdd.cucumber.suiteslicing;

import net.serenitybdd.cucumber.util.BigDecimalAverageCollector;

import java.math.BigDecimal;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;

public class TestScenarioResults {

    public final String scenarioKey;
    public final List<BigDecimal> durations;
    private final String feature;
    private final String scenario;

    public static TestScenarioResults create(TestScenarioResult testScenarioDuration) {
        return new TestScenarioResults(testScenarioDuration);
    }

    public void addDuration(BigDecimal duration) {
        durations.add(duration);
    }

    private TestScenarioResults(TestScenarioResult testScenarioResult) {
        this.durations = newArrayList(testScenarioResult.duration);
        this.scenarioKey = testScenarioResult.scenarioKey;
        this.feature = testScenarioResult.feature;
        this.scenario = testScenarioResult.scenario;
    }

    public TestScenarioResult average() {
        return new TestScenarioResult(feature, scenario, durations.stream().collect(BigDecimalAverageCollector.create()));
    }

    @Override
    public boolean equals(Object o) {
        return reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return reflectionToString(this);
    }
}
