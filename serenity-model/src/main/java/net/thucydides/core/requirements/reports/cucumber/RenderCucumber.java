package net.thucydides.core.requirements.reports.cucumber;



import io.cucumber.messages.Messages;
import io.cucumber.messages.Messages.GherkinDocument.Feature.Scenario.Examples;
import io.cucumber.messages.Messages.GherkinDocument.Feature.Step;
import io.cucumber.messages.Messages.GherkinDocument.Feature.Step.ArgumentCase;
import io.cucumber.messages.Messages.GherkinDocument.Feature.Step.DataTable;
import io.cucumber.messages.Messages.GherkinDocument.Feature.TableRow;
import io.cucumber.messages.Messages.GherkinDocument.Feature.TableRow.TableCell;
import net.thucydides.core.requirements.model.cucumber.ExampleRowResultIcon;

import java.util.ArrayList;
import java.util.List;

public class RenderCucumber {
    public static String step(Step step) {
        return step.getKeyword() + withEscapedParameterFields(step.getText()) + "  " + renderedArgument(step);
    }

    private static String renderedArgument(Step step) {
        if(step.getArgumentCase().equals(ArgumentCase.DATA_TABLE)) {
            return renderedDataTable(step.getDataTable());
        } else if(step.getArgumentCase().equals(ArgumentCase.DOC_STRING)) {
            return step.getDocString().getContent();
        }
        return "";
    }

    public static List<String> examples(List<Messages.GherkinDocument.Feature.Scenario.Examples> examples,
                                        String featureName,
                                        String scenarioName) {


        List<String> renderedExamples = new ArrayList<>();
        for (Examples exampleTable : examples) {
            renderedExamples.add(renderedExamples(exampleTable, featureName, scenarioName));
        }
        return renderedExamples;
    }

    private static String renderedExamples(Examples examples, String featureName, String scenarioName) {
        
        ExampleRowResultIcon exampleRowResultIcon = new ExampleRowResultIcon(featureName);

        StringBuffer renderedTable = new StringBuffer();
        renderExampleDescriptionOf(examples);
        renderedTable.append(renderExampleDescriptionOf(examples));
        addRow(renderedTable, examples.getTableHeader().getCellsList(), " ");
        addSeparatorCells(renderedTable, examples.getTableHeader().getCellsList().size());

        for (Messages.GherkinDocument.Feature.TableRow row : examples.getTableBodyList()) {
            addRow(renderedTable, row.getCellsList(), exampleRowResultIcon.resultToken(row.getLocation().getLine()));
        }

        return renderedTable.toString();
    }

    private static String renderExampleDescriptionOf(Examples examples) {
        StringBuffer renderedTable = new StringBuffer();

        renderedTable.append(examples.getKeyword()).append(": ");
        if (examples.getName() != null) {
            renderedTable.append(examples.getName());
        }
        renderedTable.append("  ").append(System.lineSeparator());

        if (examples.getDescription() != null) {
            renderedTable.append(System.lineSeparator())
                    .append("> ")
                    .append(examples.getDescription().trim())
                    .append(System.lineSeparator());
        }

        renderedTable.append(System.lineSeparator());

        return renderedTable.toString();
    }


    private static String renderedExamples(Examples examples) {
        StringBuffer renderedTable = new StringBuffer();
        renderedTable.append(renderExampleDescriptionOf(examples));
        addRow(renderedTable, examples.getTableHeader().getCellsList(), null);
        addSeparatorCells(renderedTable, examples.getTableHeader().getCellsCount());

        for (TableRow row : examples.getTableBodyList()) {
            addRow(renderedTable, row.getCellsList(), null);
        }

        return renderedTable.toString();
    }

    private static String renderedDataTable(DataTable dataTable) {
        StringBuffer renderedTable = new StringBuffer();
        renderedTable.append("  ").append(System.lineSeparator());

        int firstRow = 0;

        TableRow header = dataTable.getRowsList().get(0);

        if (thereAreMultipleColumnsIn(dataTable)) {
            addRow(renderedTable, header.getCellsList());
            addSeparatorCells(renderedTable, header.getCellsList().size());
            firstRow++;
      } else {
            addSeparatorCells(renderedTable, header.getCellsList().size());
        }

        for (int row = firstRow; row < dataTable.getRowsList().size(); row++) {
            addRow(renderedTable, dataTable.getRowsList().get(row).getCellsList());
        }
        return renderedTable.toString();
    }

    private static boolean thereAreMultipleColumnsIn(DataTable dataTable) {
        return dataTable.getRowsList().get(0).getCellsList().size() > 1;
    }

    private static void addSeparatorCells(StringBuffer renderedTable, int columnCount) {
        renderedTable.append("|");
        for (int col = 0; col < columnCount; col++) {
            renderedTable.append("-----------").append("|");
        }
        renderedTable.append("  ").append(System.lineSeparator());
    }

    private static void addRow(StringBuffer renderedTable, List<TableCell> cells) {
        addRow(renderedTable, cells, null);
    }

    private static void addRow(StringBuffer renderedTable, List<TableCell> cells, String statusToken) {
        renderedTable.append("|");
        for (TableCell cell : cells) {
            renderedTable.append(withEscapedParameterFields(cell.getValue())).append(" |");
        }
        if (statusToken != null) {
            renderedTable.append(statusToken + " |");
        }
        renderedTable.append("  ").append(System.lineSeparator());
    }

    private static String withEscapedParameterFields(String text) {
        return text.replace("<", "{").replace(">", "}");
    }
}
