package net.serenitybdd.cucumber.suiteslicing;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.emptySet;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class WeightedCucumberScenariosTest {

    @Test
    public void slicingMoreThinlyThanTheNumberOfScenariosShouldResultInSomeEmptySlices() {
        WeightedCucumberScenario weightedCucumberScenario = new WeightedCucumberScenario("test.feature", "featurename", "scenarioname", BigDecimal.ONE, emptySet(), 0);
        List<WeightedCucumberScenario> scenarios = Collections.singletonList(weightedCucumberScenario);
        WeightedCucumberScenarios oneScenario = new WeightedCucumberScenarios(scenarios);
        List<WeightedCucumberScenarios> weightedCucumberScenarios = oneScenario.sliceInto(100);
        assertThat(weightedCucumberScenarios, hasSize(100));
        assertThat(weightedCucumberScenarios.get(0).scenarios, hasSize(1));
        assertThat(weightedCucumberScenarios.get(0).scenarios.get(0), is(weightedCucumberScenario));
        assertThat(weightedCucumberScenarios.get(1).scenarios, hasSize(0));
        assertThat(weightedCucumberScenarios.get(99).scenarios, hasSize(0));
    }

    @Test
    public void slicingASliceIntoOneSliceOfOneShouldBeTheSameAsAllScenarios() {
        List<WeightedCucumberScenario> scenarios = Collections.singletonList(new WeightedCucumberScenario("test.feature", "featurename", "scenarioname", BigDecimal.ONE, emptySet(), 0));
        WeightedCucumberScenarios oneScenario = new WeightedCucumberScenarios(scenarios);
        WeightedCucumberScenarios fork1 = oneScenario.slice(1).of(1);
        assertThat(oneScenario, is(fork1));
    }

}