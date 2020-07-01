package net.thucydides.core.requirements.model.cucumber;

import io.cucumber.messages.Messages.GherkinDocument.Feature;
import io.cucumber.messages.Messages.GherkinDocument.Feature.Scenario;
import io.cucumber.messages.Messages.GherkinDocument.Feature.Scenario.Examples;

import java.util.Optional;

import static net.thucydides.core.requirements.model.cucumber.ScenarioDisplayOption.WithNoTitle;

public class IdentifiedExampleTable extends NamedExampleTable {
    private Feature feature;
    private String scenarioReport;
    private Scenario scenarioDefinition;
    private String exampleTableName;
    private ExampleTableInMarkdown exampleTableInMarkdown;

    protected IdentifiedExampleTable(Feature feature, Scenario scenarioDefinition, String exampleTableName) {
        this.feature = feature;
        this.scenarioReport = ScenarioReport.forScenario(scenarioDefinition.getName()).inFeature(feature);
        this.scenarioDefinition = scenarioDefinition;
        this.exampleTableName = exampleTableName;
        this.exampleTableInMarkdown = new ExampleTableInMarkdown(feature, scenarioReport, scenarioDefinition);
    }

    @Override
    public Optional<String> asExampleTable() {
        return asExampleTable(WithNoTitle);
    }

    @Override
    public Optional<String> asExampleTable(ScenarioDisplayOption withDisplayOption) {
        if (scenarioDefinition.getExamplesCount() == 0) {
            return Optional.empty();
        }

        int exampleRow = 0;
        for (Examples example : scenarioDefinition.getExamplesList()) {
            if (example.getName().equalsIgnoreCase(exampleTableName.trim())) {
                return Optional.of(exampleTableInMarkdown.renderedFormOf(example, exampleRow, withDisplayOption));
            }
            exampleRow++;
        }
        return Optional.empty();
    }
}
