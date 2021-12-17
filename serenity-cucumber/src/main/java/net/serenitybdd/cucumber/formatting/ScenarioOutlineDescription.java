package net.serenitybdd.cucumber.formatting;

import io.cucumber.messages.types.*;

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
        return scenario.getSteps().stream()
                .map(this::stepToString)
                .collect(Collectors.joining(System.lineSeparator()));
    }

    private String stepToString(Step step) {
        String phrase = step.getKeyword() + step.getText();


        if (step.getDataTable() != null) {
            DataTable table = step.getDataTable();
            String tableAsString = "";
            for (TableRow row : table.getRows()) {
                tableAsString += "|";
                tableAsString += row.getCells().stream()
                        .map(TableCell::getValue)
                        .collect(Collectors.joining(" | "));
                tableAsString += "|" + System.lineSeparator();
            }

            phrase = phrase + System.lineSeparator() + tableAsString.trim();
        }

        return phrase;
    }
}
