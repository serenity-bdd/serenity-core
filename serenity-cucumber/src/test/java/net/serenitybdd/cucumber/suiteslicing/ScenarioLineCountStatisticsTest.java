package net.serenitybdd.cucumber.suiteslicing;

import io.cucumber.gherkin.ScenarioLineCountStatistics;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ScenarioLineCountStatisticsTest {

    ScenarioLineCountStatistics stats;

    @Before
    public void setup() {
        try {
            stats = ScenarioLineCountStatistics.fromFeaturePath(new URI("classpath:samples"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void scenarioWeightForScenario() {
        assertThat(stats.scenarioWeightFor("A simple feature", "A simple scenario"), is(new BigDecimal("4")));
    }

    @Test
    public void scenarioWeightForScenarioOutline() {
        assertThat(stats.scenarioWeightFor("A simple feature that fails", "A simple failing scenario outline"), is(new BigDecimal("8")));
    }

    @Test
    public void scenarioWeightForScenarioWithBackground() {
        assertThat(stats.scenarioWeightFor("Locate a customer by personal details and Reg Number", "Locating a customer using a unique criterion"), is(new BigDecimal("4")));
    }

    @Test
    public void scenarioWeightForScenarioWithBackgroundAndScenarioOutline() {
        assertThat(stats.scenarioWeightFor("Buying things - with tables", "Buying more widgets"), is(new BigDecimal("15")));
    }

    @Test
    public void scenarioWeightForScenarioOutlineWithMultipleExamples() {
        assertThat(stats.scenarioWeightFor("Buying things - with tables", "Buying lots of widgets"), is(new BigDecimal("35")));
    }

    @Test
    public void scenarioWeightForScenarioWithBackgroundAndScenario() {
        assertThat(stats.scenarioWeightFor("Locate a customer by personal details and Reg Number", "Locating a customer using a unique criterion"), is(new BigDecimal("4")));
    }

}