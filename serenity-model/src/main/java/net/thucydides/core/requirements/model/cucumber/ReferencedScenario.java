package net.thucydides.core.requirements.model.cucumber;


import io.cucumber.core.internal.gherkin.ast.Feature;

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
        return feature.getChildren().stream()
                        .filter(scenarioDefinition -> scenarioDefinition.getName().equalsIgnoreCase(scenarioName.trim()))
                        .map(scenario -> NamedScenario.forScenarioDefinition(feature, scenario))
                        .findFirst()
                        .orElse(NamedScenario.withNoMatchingScenario());
    }
}
