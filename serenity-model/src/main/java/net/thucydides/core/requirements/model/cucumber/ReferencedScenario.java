package net.thucydides.core.requirements.model.cucumber;

import io.cucumber.messages.types.Feature;

/**
 * A scenario that is mentioned by name in a feature narrative.
 */
public class ReferencedScenario {
    private final Feature feature;

    public ReferencedScenario(Feature feature) {
        this.feature = feature;
    }

    public static ReferencedScenario in(Feature feature) {
        return new ReferencedScenario(feature);
    }

    public NamedScenario withName(String scenarioName) {
        return feature.getChildren().stream()
                        .filter(featureChild -> featureChild.getScenario() != null)
                        .filter(featureChild -> featureChild.getScenario().getName().equalsIgnoreCase(scenarioName.trim()))
                        .map(featureChild -> NamedScenario.forScenarioDefinition(feature, featureChild.getScenario()))
                        .findFirst()
                        .orElse(NamedScenario.withNoMatchingScenario());
    }
}
