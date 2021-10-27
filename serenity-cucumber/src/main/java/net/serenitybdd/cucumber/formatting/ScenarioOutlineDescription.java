package net.serenitybdd.cucumber.formatting;


import io.cucumber.messages.Messages.GherkinDocument.Feature.Scenario;
import io.cucumber.messages.Messages.GherkinDocument.Feature.Step;
import io.cucumber.messages.Messages.GherkinDocument.Feature.Step.DataTable;
import io.cucumber.messages.Messages.GherkinDocument.Feature.TableRow;
import io.cucumber.messages.Messages.GherkinDocument.Feature.TableRow.TableCell;

import java.util.stream.Collectors;

public class ScenarioOutlineDescription {
    private final Scenario scenario;

    public ScenarioOutlineDescription(Scenario scenario) {
        this.scenario = scenario;
    }

    public static ScenarioOutlineDescription from(Scenario scenario) {
        return new ScenarioOutlineDescription(scenario);
    }

    public String getDescription() {
        return scenario.getStepsList().stream().map(
                step -> stepToString(step)
        ).collect(Collectors.joining(System.lineSeparator()));
    }

    private String stepToString(Step step) {
        String phrase = step.getKeyword() + step.getText();


        if (step.hasDataTable()) {
            DataTable table = step.getDataTable();
            String tableAsString = "";
            for (TableRow row : table.getRowsList()) {
                tableAsString += "|";
                tableAsString += row.getCellsList().stream()
                        .map(TableCell::getValue)
                        .collect(Collectors.joining(" | "));
                tableAsString += "|" + System.lineSeparator();
            }

            phrase = phrase + System.lineSeparator() + tableAsString.trim();
        }

        return phrase;
    }
}
