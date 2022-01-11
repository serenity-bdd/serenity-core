package net.thucydides.core.requirements.reports.cucumber;



import io.cucumber.messages.types.*;
import net.thucydides.core.requirements.model.cucumber.ExampleRowResultIcon;

import java.util.ArrayList;
import java.util.List;

public class RenderCucumber {
    public static String step(Step step) {
        return step.getKeyword() + withEscapedParameterFields(step.getText()) + "  " + renderedArgument(step);
    }

    private static String renderedArgument(Step step) {
        if(step.getDataTable() != null) {
            return renderedDataTable(step.getDataTable());
        } else if(step.getDocString() != null) {
            return step.getDocString().getContent();
        }
        return "";
    }

    public static List<String> examples(List<Examples> examples,
                                        String featureName) {


        List<String> renderedExamples = new ArrayList<>();
        for (Examples exampleTable : examples) {
            renderedExamples.add(renderedExamples(exampleTable, featureName));
        }
        return renderedExamples;
    }

    private static String renderedExamples(Examples examples, String featureName) {
        
        ExampleRowResultIcon exampleRowResultIcon = new ExampleRowResultIcon(featureName);

        StringBuffer renderedTable = new StringBuffer();
        renderExampleDescriptionOf(examples);
        renderedTable.append(renderExampleDescriptionOf(examples));
        addRow(renderedTable, examples.getTableHeader().getCells(), " ");
        addSeparatorCells(renderedTable, examples.getTableHeader().getCells().size());

        for (TableRow row : examples.getTableBody()) {
            addRow(renderedTable, row.getCells(), exampleRowResultIcon.resultToken(row.getLocation().getLine()));
        }

        return renderedTable.toString();
    }

    private static String renderExampleDescriptionOf(Examples examples) {
        StringBuilder renderedTable = new StringBuilder();

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

    private static String renderedDataTable(DataTable dataTable) {
        StringBuffer renderedTable = new StringBuffer();
        renderedTable.append("  ").append(System.lineSeparator());

        int firstRow = 0;

        TableRow header = dataTable.getRows().get(0);

        if (thereAreMultipleColumnsIn(dataTable)) {
            addRow(renderedTable, header.getCells());
            addSeparatorCells(renderedTable, header.getCells().size());
            firstRow++;
      } else {
            addSeparatorCells(renderedTable, header.getCells().size());
        }

        for (int row = firstRow; row < dataTable.getRows().size(); row++) {
            addRow(renderedTable, dataTable.getRows().get(row).getCells());
        }
        return renderedTable.toString();
    }

    private static boolean thereAreMultipleColumnsIn(DataTable dataTable) {
        return dataTable.getRows().get(0).getCells().size() > 1;
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
            renderedTable.append(statusToken).append(" |");
        }
        renderedTable.append("  ").append(System.lineSeparator());
    }

    private static String withEscapedParameterFields(String text) {
        return text.replace("<", "{").replace(">", "}");
    }
}
