package net.serenitybdd.cucumber.suiteslicing;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class MultiRunTestStatisticsTest {

    TestStatistics statistics;

    @Before
    public void setup() {
        statistics = MultiRunTestStatistics.fromRelativePath("/statistics");
    }

    @Test
    public void recordCountShouldBeCorrect()  {
        assertThat(statistics.records(), hasSize(19));
    }

    @Test
    public void scenarioWeightForShouldReturnAverageOfDurationsForKnownScenario()  {
        assertThat(statistics.scenarioWeightFor("Using Background Steps", "Running a scenario with a Before clause"), is(new BigDecimal("34.03")));
    }

    @Test
    public void scenarioWeightForShouldReturnAverageAllDurationsForUnknownScenario()  {
        assertThat(statistics.scenarioWeightFor("Yo", "I don't exist matey"), is(new BigDecimal("5.53")));
    }

}