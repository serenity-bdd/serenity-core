package io.cucumber.junit;

import org.junit.Test;
import org.junit.runner.Description;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FeatureRunnerExtractorsTest {

    @Test
    public void extractFeatureNameShouldReturnTheNameOfTheFeatureWithSloppyNamingConvention() {
        performExpectationsWith("Feature : my super feature", "my super feature");
    }

    @Test
    public void extractFeatureNameShouldReturnTheNameOfANormallyNamedFeature() {
        performExpectationsWith("Feature: my super feature", "my super feature");
    }

    @Test
    public void extractFeatureNameShouldReturnTheNameOfTheFeatureThatHasAColon() {
        performExpectationsWith("Feature: my super feature: is with colon", "my super feature: is with colon");
    }

    private void performExpectationsWith(String displayName, String expectation) {
        FeatureRunner runner = mock(FeatureRunner.class);
        Description description = mock(Description.class);
        when(description.getDisplayName()).thenReturn(displayName);
        when(runner.getDescription()).thenReturn(description);
        String name = FeatureRunnerExtractors.extractFeatureName(runner);
        assertThat(name, is(expectation));
    }


}