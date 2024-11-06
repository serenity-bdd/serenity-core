package io.cucumber.junit.platform.engine;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.support.descriptor.ClasspathResourceSource;

import net.serenitybdd.cucumber.suiteslicing.TestStatistics;


class TestWeightCalculator {

    private static TestStatistics statistics;

    static int calculateWeight(TestDescriptor descriptor) {
        return getEstimatedTestDuration(descriptor).intValue();
    }

    private static BigDecimal getEstimatedTestDuration(TestDescriptor descriptor) {
        if (statistics == null) {
            statistics = TestStatistics.from(SystemEnvironmentVariables.currentEnvironmentVariables(),
                                             List.of(URI.create("classpath:" + getTopFeatureDirectory(descriptor))));
        }
        String featureName = descriptor.getParent().map(TestDescriptor::getDisplayName).orElseThrow();
        String scenarioName = descriptor.getDisplayName();
        return statistics.scenarioWeightFor(featureName, scenarioName);
    }

    private static String getTopFeatureDirectory(TestDescriptor descriptor) {
        ClasspathResourceSource resource = (ClasspathResourceSource) descriptor.getSource().orElseThrow();
        return resource.getClasspathResourceName().split("/")[0];
    }
}

