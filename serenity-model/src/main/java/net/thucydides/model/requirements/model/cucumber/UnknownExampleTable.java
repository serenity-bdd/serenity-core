package net.thucydides.model.requirements.model.cucumber;

import java.util.Optional;

public class UnknownExampleTable extends NamedExampleTable {

    @Override
    public Optional<String> asExampleTable() {
        return Optional.empty();
    }

    @Override
    public Optional<String> asExampleTable(ScenarioDisplayOption displayOption) {
        return Optional.empty();
    }
}
