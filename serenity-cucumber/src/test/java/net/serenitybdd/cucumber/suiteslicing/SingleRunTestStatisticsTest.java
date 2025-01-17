package net.serenitybdd.cucumber.suiteslicing;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SingleRunTestStatisticsTest {

    TestStatistics statistics;

    @Before
    public void setup() {
        statistics = SingleRunTestStatistics.fromFileName("/statistics/smoke-test-results-run-1.csv");
    }

    @Test
    public void recordCountShouldBeCorrect() throws Exception {
        assertThat(statistics.records(), hasSize(19));
    }

    @Test
    public void scenarioWeightForShouldReturnExactDurationForKnownScenario() throws Exception {
        assertThat(statistics.scenarioWeightFor("Using Background Steps", "Running a scenario with a Before clause"), is(new BigDecimal("38.49")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void scenarioWeightForShouldReturnAverageDurationForUnknownScenario() throws Exception {
        statistics.scenarioWeightFor("Yo", "I don't exist matey");
    }

}