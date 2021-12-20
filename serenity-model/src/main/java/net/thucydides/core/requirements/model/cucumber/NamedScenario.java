package net.thucydides.core.requirements.model.cucumber;

import io.cucumber.messages.types.Feature;
import io.cucumber.messages.types.Scenario;

import java.util.Optional;

public abstract class NamedScenario {
    public static NamedScenario forScenarioDefinition(Feature feature, Scenario scenarioDefinition) {
        return new IdentifiedScenario(feature, scenarioDefinition);
    }

    public static NamedScenario withNoMatchingScenario() { return new UnknownScenario(); }


    /**
     * Return the Given..When..Then part of the scenario
     */
    public abstract Optional<String> asGivenWhenThen(ScenarioDisplayOption displayOption);
    public abstract Optional<String> asGivenWhenThen();

    /**
     * Return the example table part of the scenario outline
     */
    public abstract Optional<String> asExampleTable();

    /**
     * Return the example table part of the scenario outline
     */
    public abstract Optional<String> asExampleTable(ScenarioDisplayOption displayOption);

}
