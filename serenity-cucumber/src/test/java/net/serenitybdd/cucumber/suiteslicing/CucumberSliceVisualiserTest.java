package net.serenitybdd.cucumber.suiteslicing;

import net.serenitybdd.cucumber.gherkin.ScenarioLineCountStatistics;
import net.serenitybdd.annotations.Narrative;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.model.util.EnvironmentVariables;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;

@Narrative(text = "There are no assertions in this test, but this is a useful tool for generating a visualisation of how a specific test slicing "
                  + "configuration will be executed as part of a running test. The test methods below allow a number of parameters to be input and when the test is run"
                  + "a json file will be produced into the serenity output directory that shows how the tests have been sliced."
                  + "Note that test slicing will always be 100% deterministic given the same set of inputs "
                  + "which makes this mechanism an excellent choice for running on grid of multiple servers, each running a mutually exclusive slice."
                  + "Variables that can be input here include feature root, number of slices, number of forks and test statistics (line count based or actual run data)")
public class CucumberSliceVisualiserTest {

    private TestStatistics HISTORIC_RUN_STATISTICS;
    private TestStatistics LINE_COUNT_STATISTICS;
    private EnvironmentVariables environmentVariables;
    private CucumberScenarioVisualiser cucumberScenarioVisualiser;
    private static final String FEATURE_ROOT = "classpath:smoketests";


    @Before
    public void setUp() throws Exception{
        HISTORIC_RUN_STATISTICS = MultiRunTestStatistics.fromRelativePath("/statistics");
        LINE_COUNT_STATISTICS = ScenarioLineCountStatistics.fromFeaturePath(new URI(FEATURE_ROOT));
        environmentVariables = SystemEnvironmentVariables.currentEnvironmentVariables();
        cucumberScenarioVisualiser = new CucumberScenarioVisualiser(environmentVariables);
    }

    @Test
    public void visualise1SliceWith4Forks() throws Exception {
        cucumberScenarioVisualiser.visualise(new URI(FEATURE_ROOT), 4, 2, HISTORIC_RUN_STATISTICS);
    }

    @Test
    public void visualise4SlicesWith2Forks() throws Exception {
        cucumberScenarioVisualiser.visualise(new URI(FEATURE_ROOT), 4, 2, HISTORIC_RUN_STATISTICS);
    }

    @Test
    public void visualise5SlicesWith1ForkBasedOnRunStats() throws Exception {
        cucumberScenarioVisualiser.visualise(new URI(FEATURE_ROOT), 5, 1, HISTORIC_RUN_STATISTICS);
    }

    @Test
    public void visualise5SlicesWith1ForkBasedOnLineCount() throws Exception {
        cucumberScenarioVisualiser.visualise(new URI(FEATURE_ROOT), 5, 1, LINE_COUNT_STATISTICS);
    }


}
