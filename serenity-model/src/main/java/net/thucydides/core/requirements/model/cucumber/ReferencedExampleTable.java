package net.thucydides.core.requirements.model.cucumber;


import io.cucumber.core.internal.gherkin.ast.Feature;
import io.cucumber.core.internal.gherkin.ast.ScenarioDefinition;
import io.cucumber.core.internal.gherkin.ast.ScenarioOutline;

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
                        .filter(scenarioDefinition -> featureContainsExampleTableWithName(exampleTableName))
                        .map(scenarioDefinition -> NamedExampleTable.forScenarioDefinition(feature, scenarioDefinition, exampleTableName))
                        .findFirst()
                        .orElse(NamedExampleTable.withNoMatchingScenario());
    }

    private boolean featureContainsExampleTableWithName(String exampleTableName) {
        return feature.getChildren().stream()
                                    .anyMatch(
                                            scenarioDefinition -> scenarioContainsExampleTableWithName(scenarioDefinition, exampleTableName)
                                    );
    }

    private boolean scenarioContainsExampleTableWithName(ScenarioDefinition scenarioDefinition, String exampleTableName) {
        if (!(scenarioDefinition instanceof ScenarioOutline)) { return false; }

        return ((ScenarioOutline) scenarioDefinition).getExamples().stream()
                .anyMatch(
                        examplesTable -> examplesTable.getName().equalsIgnoreCase(exampleTableName.trim())
                );
    }
}
