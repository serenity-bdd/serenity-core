package net.serenitybdd.cucumber.suiteslicing;


import org.junit.Before;
import org.junit.Test;

import java.net.URI;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

public class CucumberSuiteSlicerTest {

    private TestStatistics testStatistics;
    private CucumberSuiteSlicer cucumberSuiteSlicer;
    MatchingCucumberScenario expectedScenario1;
    MatchingCucumberScenario expectedScenario2;

    @Before
    public void setup() throws Exception {
        testStatistics = new DummyStatsOfWeightingOne();
        cucumberSuiteSlicer = new CucumberSuiteSlicer(asList(new URI("classpath:samples/simple_table_based_scenario.feature")), testStatistics);
        expectedScenario1 = MatchingCucumberScenario.with()
            .featurePath("simple_table_based_scenario.feature")
            .feature("Buying things - with tables")
            .scenario("Buying lots of widgets")
            .tags("@shouldPass");

        expectedScenario2 = MatchingCucumberScenario.with()
            .featurePath("simple_table_based_scenario.feature")
            .feature("Buying things - with tables")
            .scenario("Buying more widgets");
    }

    @Test
    public void shouldReturnOnlyScenariosWithSpecifiedTags() {
        assertThat(cucumberSuiteSlicer.scenarios(1, 1, 1, 1, asList("@shouldPass")).scenarios, contains(expectedScenario1));
    }

    @Test
    public void noSuppliedTagsMeansReturnAllScenarios() {
        assertThat(cucumberSuiteSlicer.scenarios(1, 1, 1, 1, asList()).scenarios, contains(expectedScenario1, expectedScenario2));
    }

    @Test
    public void shouldSupportNotInTheTagExpression() {
        assertThat(cucumberSuiteSlicer.scenarios(1, 1, 1, 1, asList("not @shouldPass")).scenarios, contains(expectedScenario2));
    }
    @Test
    public void shouldSupportOldExclusionSyntaxInTheTagExpression() {
        assertThat(cucumberSuiteSlicer.scenarios(1, 1, 1, 1, asList("~@shouldPass")).scenarios, contains(expectedScenario2));
    }
}
