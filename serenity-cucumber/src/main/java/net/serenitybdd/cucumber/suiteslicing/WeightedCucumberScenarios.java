package net.serenitybdd.cucumber.suiteslicing;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import static java.math.BigDecimal.ZERO;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.ObjectUtils.compare;
import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;

/**
 * Represents a collection of cucumber scenarios.
 * Can split itself up into a number of smaller WeightedCucumberScenarios.
 * Can return a new WeightedCucumberScenarios that has some scenarios filtered out.
 */
public class WeightedCucumberScenarios {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeightedCucumberScenarios.class);
    public final BigDecimal totalWeighting;
    public final List<WeightedCucumberScenario> scenarios;

    public WeightedCucumberScenarios(List<WeightedCucumberScenario> scenarios) {
        this.scenarios = scenarios;
        this.totalWeighting = scenarios.stream().map(WeightedCucumberScenario::weighting).reduce(ZERO, BigDecimal::add);
    }

    public SliceBuilder slice(int sliceNumber) {
        return new SliceBuilder(sliceNumber, this);
    }

    public List<WeightedCucumberScenarios> sliceInto(int sliceCount) {
        BigDecimal totalWeight = scenarios.stream().map(WeightedCucumberScenario::weighting).reduce(ZERO, BigDecimal::add);
        BigDecimal averageWeightPerSlice = totalWeight.divide(new BigDecimal(sliceCount), 2, RoundingMode.HALF_UP);
        LOGGER.debug("Total weighting for {} scenarios is {}, split across {} slices provides average weighting per slice of {}", scenarios.size(), totalWeight, sliceCount, averageWeightPerSlice);

        List<List<WeightedCucumberScenario>> allScenarios = IntStream.rangeClosed(1, sliceCount).mapToObj(initialiseAs -> new ArrayList<WeightedCucumberScenario>()).collect(toList());

        scenarios.stream()
            .sorted(bySlowestFirst().thenComparing(byFeaturePathAscending()))
            .forEach(scenario -> allScenarios.stream().min(byLowestSumOfDurationFirst()).get().add(scenario));

        return allScenarios.stream().map(WeightedCucumberScenarios::new).collect(toList());
    }

    public ScenarioFilter createFilterContainingScenariosIn(String featureName) {
        LOGGER.debug("Filtering for scenarios in feature {}", featureName);
        List<String> scenarios = this.scenarios.stream()
            .filter(scenario -> {
                boolean matches = scenario.feature.equals(featureName);
                LOGGER.debug("Scenario {} matches {} -> {}", scenario.feature, featureName, matches);
                return matches;
            })
            .map(scenario -> scenario.scenario)
            .collect(toList());
        if (scenarios.isEmpty()) {
            throw new IllegalArgumentException("Can't find feature '" + featureName + "' in this slice");
        }
        return ScenarioFilter.onScenarios(scenarios);

    }

    public WeightedCucumberScenarios filter(Predicate<WeightedCucumberScenario> predicate) {
        return new WeightedCucumberScenarios(scenarios.stream().filter(predicate).collect(toList()));
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
        return ToStringBuilder.reflectionToString(this);
    }

    private static Comparator<WeightedCucumberScenario> bySlowestFirst() {
        return (item1, item2) -> compare(item2.weighting(), item1.weighting());
    }

    /** Ensure the order of scenarios with the same weighting. This is to prevent scenarios getting skipped from
     * batch or fork. Use featurePath due to unique file name.
     * */
    private static Comparator<WeightedCucumberScenario> byFeaturePathAscending() {
        return (item1, item2) -> compare(item1.featurePath, item2.featurePath);
    }

    private static Comparator<List<WeightedCucumberScenario>> byLowestSumOfDurationFirst() {
        return (item1, item2) -> compare(item1.stream().map(WeightedCucumberScenario::weighting).reduce(ZERO, BigDecimal::add),
                                         item2.stream().map(WeightedCucumberScenario::weighting).reduce(ZERO, BigDecimal::add));
    }

    public int totalScenarioCount() {
        return scenarios.stream().map(scenario -> scenario.scenarioCount).reduce(0, Integer::sum);
    }
}
