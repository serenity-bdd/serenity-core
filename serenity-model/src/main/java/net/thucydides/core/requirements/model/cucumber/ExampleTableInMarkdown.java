package net.thucydides.core.requirements.model.cucumber;

import gherkin.ast.Examples;
import gherkin.ast.Feature;
import gherkin.ast.ScenarioDefinition;

import static net.thucydides.core.requirements.model.cucumber.ScenarioDisplayOption.WithTitle;

public class ExampleTableInMarkdown {

    private final Feature feature;
    private final String scenarioReport;
    private final ScenarioDefinition scenarioDefinition;

    public ExampleTableInMarkdown(Feature feature, String scenarioReport, ScenarioDefinition scenarioDefinition) {
        this.feature = feature;
        this.scenarioReport = scenarioReport;
        this.scenarioDefinition = scenarioDefinition;
    }

    public String renderedFormOf(Examples exampleTable, int exampleRow, ScenarioDisplayOption displayOption) {

        ExampleRowResultIcon exampleRowCounter = new ExampleRowResultIcon(feature.getName(),scenarioDefinition.getName(), exampleRow);

        StringBuilder renderedExampleTable = new StringBuilder();

        String tableName = RenderedExampleTable.nameFor(exampleTable);
        if (tableName.isEmpty()) {
            tableName = scenarioDefinition.getName();
        }
        if (displayOption == WithTitle) {
            String exampleTitle = "### " + tableName;
            renderedExampleTable.append(exampleTitle);
        }
        renderedExampleTable.append(System.lineSeparator());
        renderedExampleTable.append(RenderedExampleTable.descriptionFor(exampleTable));
        renderedExampleTable.append(RenderedExampleTable.renderedTable(exampleTable, exampleRowCounter));
        renderedExampleTable.append(System.lineSeparator()).append("[<i class=\"fa fa-info-circle\"></i> More details](" + scenarioReport + ")").append(System.lineSeparator());

        return renderedExampleTable.toString();
    }
}
