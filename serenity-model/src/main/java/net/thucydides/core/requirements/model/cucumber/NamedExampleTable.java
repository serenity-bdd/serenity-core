package net.thucydides.core.requirements.model.cucumber;

import io.cucumber.messages.types.Feature;
import io.cucumber.messages.types.Scenario;

import java.util.Optional;

public abstract class NamedExampleTable {
    public static NamedExampleTable forScenarioDefinition(Feature feature, Scenario scenarioDefinition, String exampleName) {
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
