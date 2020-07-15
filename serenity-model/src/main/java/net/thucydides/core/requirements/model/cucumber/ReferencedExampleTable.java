package net.thucydides.core.requirements.model.cucumber;

import io.cucumber.messages.Messages.GherkinDocument.Feature;
import io.cucumber.messages.Messages.GherkinDocument.Feature.Scenario;

/**
 * An example table that is mentioned by name in a feature narrative.
 */
public class ReferencedExampleTable {
    private Feature feature;

    public ReferencedExampleTable(Feature feature) {
        this.feature = feature;
    }

    public static ReferencedExampleTable in(Feature feature) {
        return new ReferencedExampleTable(feature);
    }

    public NamedExampleTable withName(String exampleTableName) {
        return feature.getChildrenList().stream()
                        .filter(featureChild -> featureChild.hasScenario())
                        .filter(scenarioDefinition -> featureContainsExampleTableWithName(exampleTableName))
                        .map(featureChild -> NamedExampleTable.forScenarioDefinition(feature, featureChild.getScenario(), exampleTableName))
                        .findFirst()
                        .orElse(NamedExampleTable.withNoMatchingScenario());
    }

    private boolean featureContainsExampleTableWithName(String exampleTableName) {
        return feature.getChildrenList().stream()
                                    .anyMatch(
                                            featureChild -> scenarioContainsExampleTableWithName(featureChild.getScenario(), exampleTableName)
                                    );
    }

    private boolean scenarioContainsExampleTableWithName(Scenario scenario, String exampleTableName) {
        if (scenario.getExamplesCount() == 0) { return false; }

        return scenario.getExamplesList().stream()
                .anyMatch(
                        examplesTable -> examplesTable.getName().equalsIgnoreCase(exampleTableName.trim())
                );
    }
}
