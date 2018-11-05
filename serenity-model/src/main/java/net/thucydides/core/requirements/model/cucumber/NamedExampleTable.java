package net.thucydides.core.requirements.model.cucumber;

import gherkin.ast.Examples;
import gherkin.ast.Feature;
import gherkin.ast.ScenarioDefinition;

import java.util.Optional;

import static net.thucydides.core.requirements.model.cucumber.ScenarioDisplayOption.WithTitle;

public abstract class NamedExampleTable {
    public static NamedExampleTable forScenarioDefinition(Feature feature, ScenarioDefinition scenarioDefinition, String exampleName) {
        return new IdentifiedExampleTable(feature, scenarioDefinition, exampleName);
    }

    public static NamedExampleTable withNoMatchingScenario() { return new UnknownExampleTable(); }

    /**
     * Return the example table part of the scenario outline
     */
    public abstract Optional<String> asExampleTable();

    /**
     * Return the example table part of the scenario outline
     */
    public abstract Optional<String> asExampleTable(ScenarioDisplayOption displayOption);


}
