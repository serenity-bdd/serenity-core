package net.thucydides.model.requirements.model.cucumber;

import io.cucumber.messages.types.Examples;
import io.cucumber.messages.types.Feature;
import io.cucumber.messages.types.Scenario;

import java.util.Optional;

import static net.thucydides.model.requirements.model.cucumber.ScenarioDisplayOption.WithNoTitle;

public class IdentifiedExampleTable extends NamedExampleTable {
    private final Feature feature;
    private final Optional<Scenario> scenarioDefinition;
    private final String exampleTableName;
    private final ExampleTableInMarkdown exampleTableInMarkdown;

    protected IdentifiedExampleTable(Feature feature, Optional<Scenario> scenarioDefinition, String exampleTableName) {
        this.feature = feature;
        String scenarioReport = null;
        if(scenarioDefinition.isPresent()) {
            scenarioReport = ScenarioReport.forScenario(scenarioDefinition.get().getName()).inFeature(feature);
        }
        this.scenarioDefinition = scenarioDefinition;
        this.exampleTableName = exampleTableName;
        this.exampleTableInMarkdown = new ExampleTableInMarkdown(feature, scenarioReport,
                    scenarioDefinition.isPresent() ? scenarioDefinition.get().getName(): "");
    }

    @Override
    public Optional<String> asExampleTable() {
        return asExampleTable(WithNoTitle);
    }

    @Override
    public Optional<String> asExampleTable(ScenarioDisplayOption withDisplayOption) {
        if(scenarioDefinition.isPresent()) {
            if (scenarioDefinition.get().getExamples().isEmpty()) {
                return Optional.empty();
            }
            int exampleRow = 0;
            for (Examples example : scenarioDefinition.get().getExamples()) {
                if (example.getName().equalsIgnoreCase(exampleTableName.trim())) {
                    return Optional.of(exampleTableInMarkdown.renderedFormOf(example, exampleRow, withDisplayOption));
                }
                exampleRow++;
            }
        }
        return Optional.empty();
    }
}
