package net.serenitybdd.cucumber.suiteslicing;

import io.cucumber.gherkin.ScenarioLineCountStatistics;
import net.thucydides.model.util.EnvironmentVariables;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

import static net.thucydides.model.ThucydidesSystemProperty.SERENITY_TEST_STATISTICS_DIR;

public interface TestStatistics {

    BigDecimal scenarioWeightFor(String feature, String scenario);

    List<TestScenarioResult> records();

    static TestStatistics from(EnvironmentVariables environmentVariables, List<URI> featurePaths) {
        String directory = environmentVariables.getProperty(SERENITY_TEST_STATISTICS_DIR);
        if (directory == null) {
            return ScenarioLineCountStatistics.fromFeaturePaths(featurePaths);
        } else {
            return MultiRunTestStatistics.fromRelativePath(directory);
        }
    }


}
