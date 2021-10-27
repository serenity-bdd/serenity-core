package net.serenitybdd.cucumber.suiteslicing;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.math.BigDecimal;
import java.util.Set;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;

public class WeightedCucumberScenario {

    public final String featurePath;
    public final String feature;
    public final String scenario;
    public final int scenarioCount;
    public final BigDecimal weighting;
    public final Set<String> tags;

    public WeightedCucumberScenario(String featurePath, String feature, String scenario, BigDecimal weighting, Set<String> tags, int scenarioCount) {
        this.featurePath = featurePath;
        this.feature = feature;
        this.scenario = scenario;
        this.weighting = weighting;
        this.tags = tags;
        this.scenarioCount = scenarioCount;
    }

    public BigDecimal weighting() {
        return weighting;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return reflectionEquals(this, obj);
    }

    @Override
    public String toString() {
        return reflectionToString(this);
    }
}
