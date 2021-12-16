package net.serenitybdd.cucumber.suiteslicing;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;


public class MatchingCucumberScenario extends TypeSafeMatcher<WeightedCucumberScenario> {

    private String featurePath;
    private String feature;
    private String scenario;
    private Set<String> tags;

    private MatchingCucumberScenario() {
    }

    public static MatchingCucumberScenario with() {
        return new MatchingCucumberScenario();
    }

    public MatchingCucumberScenario featurePath(String featurePath) {
        this.featurePath = featurePath;
        return this;
    }

    public MatchingCucumberScenario feature(String feature) {
        this.feature = feature;
        return this;
    }

    public MatchingCucumberScenario scenario(String scenario) {
        this.scenario = scenario;
        return this;
    }

    @Override
    protected boolean matchesSafely(WeightedCucumberScenario weightedCucumberScenario) {
        return (feature == null || feature.equals(weightedCucumberScenario.feature)) &&
               (featurePath == null || featurePath.equals(weightedCucumberScenario.featurePath)) &&
               (scenario == null || scenario.equals(weightedCucumberScenario.scenario)) &&
               (tags == null || tags.equals(weightedCucumberScenario.tags));
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("A WeightedCucumberScenario matching: featurePath: " + featurePath +
                               ", feature: " + feature);
    }

    public MatchingCucumberScenario tags(String... tags) {
        this.tags = new HashSet<>(asList(tags));
        return this;
    }
}
