package net.serenitybdd.cucumber.suiteslicing;

import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.MatcherAssert.*;

public class CucumberScenarioLoaderTest {

    private TestStatistics testStatistics;

    @Before
    public void setup() {
        testStatistics = new DummyStatsOfWeightingOne();
    }

    @Test
    public void shouldEnsureThatFeaturesWithBackgroundsDontCountThemAsScenarios() throws Exception {
        WeightedCucumberScenarios weightedCucumberScenarios = new CucumberScenarioLoader(newArrayList(new URI("classpath:samples/simple_table_based_scenario.feature")), testStatistics).load();
        assertThat(weightedCucumberScenarios.scenarios, containsInAnyOrder(MatchingCucumberScenario.with()
                                                                               .featurePath("simple_table_based_scenario.feature")
                                                                               .feature("Buying things - with tables")
                                                                               .scenario("Buying lots of widgets"),
                                                                           MatchingCucumberScenario.with()
                                                                               .featurePath("simple_table_based_scenario.feature")
                                                                               .feature("Buying things - with tables")
                                                                               .scenario("Buying more widgets")));
    }

    @Test
    public void shouldLoadFeatureAndScenarioTagsOntoCorrectScenarios() throws Exception {
        WeightedCucumberScenarios weightedCucumberScenarios = new CucumberScenarioLoader(newArrayList(new URI("classpath:samples/simple_table_based_scenario.feature")), testStatistics).load();

        assertThat(weightedCucumberScenarios.scenarios, contains(MatchingCucumberScenario.with()
                                                                     .featurePath("simple_table_based_scenario.feature")
                                                                     .feature("Buying things - with tables")
                                                                     .scenario("Buying lots of widgets")
                                                                     .tags("@shouldPass"),
                                                                 MatchingCucumberScenario.with()
                                                                     .featurePath("simple_table_based_scenario.feature")
                                                                     .feature("Buying things - with tables")
                                                                     .scenario("Buying more widgets")
                                                                     .tags()));
    }

    @Test
    public void shouldIncludeExamplesTagsOntoScenarios() throws Exception {
        WeightedCucumberScenarios weightedCucumberScenarios = new CucumberScenarioLoader(newArrayList(new URI("classpath:samples/tagged_example_tables.feature")), testStatistics).load();

        assertThat(weightedCucumberScenarios.scenarios, contains(MatchingCucumberScenario.with()
                                                                    .featurePath("tagged_example_tables.feature")
                                                                    .feature("Tagged Tables")
                                                                    .scenario("This scenario should have two tables")
                                                                    .tags("@small", "@big")));
    }

    @Test
    public void scenariosOfAllSlicesBetweenAllJvmShouldBeTheSame() throws URISyntaxException {
        List<URI> featurePaths = newArrayList(new URI("classpath:samples/feature_pending_tag.feature"),
            new URI("classpath:samples/multiple_jira_issues.feature"),
            new URI("classpath:samples/multiple_scenarios.feature"),
            new URI("classpath:samples/scenario_with_table_in_background_steps.feature"),
            new URI("classpath:samples/tagged_example_tables.feature"));
        int sliceCount = 5;

        List<WeightedCucumberScenarios> jvm1Slices = new CucumberScenarioLoader(featurePaths, new DummyStatsOfWeightingOne()).load()
            .sliceInto(sliceCount);

        List<WeightedCucumberScenarios> jvm2Slices = new CucumberScenarioLoader(featurePaths, new DummyStatsOfWeightingOne()).load()
            .sliceInto(sliceCount);

        for (int i = 0; i < jvm1Slices.size(); i++) {
            assertThat(jvm1Slices.get(i).scenarios, contains(buildMatchingCucumberScenario(jvm2Slices.get(i))));
        }
    }

    @Test
    public void shouldCorrectlyParseAllTags() throws Exception {
        WeightedCucumberScenarios weightedCucumberScenarios = new CucumberScenarioLoader(newArrayList(new URI("classpath:samples/jira_issue.feature")), testStatistics).load();

        assertThat(weightedCucumberScenarios.scenarios, contains(MatchingCucumberScenario.with()
                .featurePath("jira_issue.feature")
                .feature("Basic Arithmetic")
                .scenario("Addition")
                .tags("@issues:ISSUE-456,ISSUE-001", "@issues:ISSUE-123,ISSUE-789", "@foo")));
    }

    private MatchingCucumberScenario[] buildMatchingCucumberScenario(WeightedCucumberScenarios weightedCucumberScenarios) {
        List<MatchingCucumberScenario> list = new ArrayList<>();
        weightedCucumberScenarios.scenarios.forEach(s -> {
            MatchingCucumberScenario match = MatchingCucumberScenario.with()
                .feature(s.feature)
                .featurePath(s.featurePath)
                .scenario(s.scenario);

            list.add(match);
        });

        return list.toArray(new MatchingCucumberScenario[list.size()]);
    }
}
