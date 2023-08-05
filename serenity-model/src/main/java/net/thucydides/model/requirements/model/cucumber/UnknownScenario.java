package net.thucydides.model.requirements.model.cucumber;

import java.util.Optional;

public class UnknownScenario extends NamedScenario {
    @Override
    public Optional<String> asGivenWhenThen(ScenarioDisplayOption displayOption) {
        return Optional.empty();
    }

    @Override
    public Optional<String> asGivenWhenThen() {
        return Optional.empty();
    }

    @Override
    public Optional<String> asExampleTable() {
        return Optional.empty();
    }

    @Override
    public Optional<String> asExampleTable(ScenarioDisplayOption displayOption) {
        return Optional.empty();
    }
}
