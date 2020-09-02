package net.thucydides.core.requirements.model.cucumber;


import io.cucumber.messages.Messages.GherkinDocument.Feature;

/**
 * A scenario that is mentioned by name in a feature narrative.
 */
public class ReferencedScenario {
    private Feature feature;

    public ReferencedScenario(Feature feature) {
        this.feature = feature;
    }

    public static ReferencedScenario in(Feature feature) {
        return new ReferencedScenario(feature);
    }

    public NamedScenario withName(String scenarioName) {
        return feature.getChildrenList().stream()
                        .filter(featureChild -> featureChild.hasScenario())
                        .filter(featureChild -> featureChild.getScenario().getName().equalsIgnoreCase(scenarioName.trim()))
                        .map(featureChild -> NamedScenario.forScenarioDefinition(feature, featureChild.getScenario()))
                        .findFirst()
                        .orElse(NamedScenario.withNoMatchingScenario());
    }
}
