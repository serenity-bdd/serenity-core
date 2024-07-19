package net.thucydides.model.requirements.model.cucumber;

import io.cucumber.messages.types.Feature;
import io.cucumber.messages.types.Scenario;

import java.util.Optional;

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
        return feature.getChildren().stream()
                        .filter(featureChild -> featureChild.getScenario() != null)
                        .filter(scenarioDefinition -> featureContainsExampleTableWithName(exampleTableName))
                        .map(featureChild -> NamedExampleTable.forScenarioDefinition(feature, featureChild.getScenario(), exampleTableName))
                        .findFirst()
                        .orElse(NamedExampleTable.withNoMatchingScenario());
    }

    private boolean featureContainsExampleTableWithName(String exampleTableName) {
        return feature.getChildren().stream()
                                    .anyMatch(
                                            featureChild -> scenarioContainsExampleTableWithName(featureChild.getScenario(), exampleTableName)
                                    );
    }

    private boolean scenarioContainsExampleTableWithName(Optional<Scenario> scenario, String exampleTableName) {
        if(scenario.isPresent()) {
            if (scenario.get().getExamples() != null && scenario.get().getExamples().isEmpty()) {
                return false;
            }

            return scenario.get().getExamples().stream()
                    .anyMatch(
                            examplesTable -> examplesTable.getName().equalsIgnoreCase(exampleTableName.trim())
                    );
        } else {
            return false;
        }
    }
}
