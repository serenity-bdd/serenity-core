package net.serenitybdd.cucumber.suiteslicing;

import net.thucydides.core.util.EnvironmentVariables;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

import static net.thucydides.core.ThucydidesSystemProperty.SERENITY_TEST_STATISTICS_DIR;

public interface TestStatistics {

    BigDecimal scenarioWeightFor(String feature, String scenario);

    List<TestScenarioResult> records();

    
    public static TestStatistics from(EnvironmentVariables environmentVariables, List<URI> featurePaths) {
        String directory = environmentVariables.getProperty(SERENITY_TEST_STATISTICS_DIR);
        if (directory == null) {
            return ScenarioLineCountStatistics.fromFeaturePaths(featurePaths);
        } else {
            return MultiRunTestStatistics.fromRelativePath(directory);
        }
    }


}
