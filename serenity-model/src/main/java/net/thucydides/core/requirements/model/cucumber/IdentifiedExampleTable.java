package net.thucydides.core.requirements.model.cucumber;

import io.cucumber.core.internal.gherkin.ast.Examples;
import io.cucumber.core.internal.gherkin.ast.Feature;
import io.cucumber.core.internal.gherkin.ast.ScenarioDefinition;
import io.cucumber.core.internal.gherkin.ast.ScenarioOutline;

import java.util.Optional;

import static net.thucydides.core.requirements.model.cucumber.ScenarioDisplayOption.WithNoTitle;

public class IdentifiedExampleTable extends NamedExampleTable {
    private Feature feature;
    private String scenarioReport;
    private ScenarioDefinition scenarioDefinition;
    private String exampleTableName;
    private ExampleTableInMarkdown exampleTableInMarkdown;

    protected IdentifiedExampleTable(Feature feature, ScenarioDefinition scenarioDefinition, String exampleTableName) {
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
        if (!(scenarioDefinition instanceof ScenarioOutline)) {
            return Optional.empty();
        }

        ScenarioOutline scenarioOutline = (ScenarioOutline) scenarioDefinition;

        int exampleRow = 0;
        for (Examples example : scenarioOutline.getExamples()) {
            if (example.getName().equalsIgnoreCase(exampleTableName.trim())) {
                return Optional.of(exampleTableInMarkdown.renderedFormOf(example, exampleRow, withDisplayOption));
            }
            exampleRow++;
        }
        return Optional.empty();
    }
}
