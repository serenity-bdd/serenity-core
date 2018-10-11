package net.thucydides.core.requirements.model.cucumber;

import gherkin.ast.Examples;
import gherkin.ast.Feature;
import gherkin.ast.ScenarioDefinition;
import gherkin.ast.ScenarioOutline;
import net.thucydides.core.requirements.reports.cucumber.RenderCucumber;

import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.System.lineSeparator;
import static net.thucydides.core.requirements.model.cucumber.ScenarioDisplayOption.WithNoTitle;
import static net.thucydides.core.requirements.model.cucumber.ScenarioDisplayOption.WithTitle;

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
