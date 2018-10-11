package net.thucydides.core.requirements.reports.cucumber;

import gherkin.ast.*;
import net.thucydides.core.requirements.model.cucumber.ExampleRowResultIcon;
import net.thucydides.core.requirements.reports.RequirementsOutcomes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RenderCucumber {
    public static String step(Step step) {
        return step.getKeyword() + withEscapedParameterFields(step.getText()) + "  " + renderedArgument(step.getArgument());
    }

    private static String renderedArgument(Node argument) {
        if (argument instanceof DataTable) {
            return renderedDataTable((DataTable)argument);
        } else if (argument instanceof Examples) {
            return renderedExamples((Examples)argument);
        }
        return "";
    }

    public static List<String> examples(List<Examples> examples,
                                        String featureName,
                                        String scenarioName) {


        List<String> renderedExamples = new ArrayList<>();
        int exampleTableNumber = 0;
        for(Examples exampleTable : examples) {
            renderedExamples.add(renderedExamples(exampleTable, featureName, scenarioName, exampleTableNumber++));
        }
        return renderedExamples;
    }

    private static String renderedExamples(Examples examples, String featureName, String scenarioName, int exampleTableNumber) {

        ExampleRowResultIcon exampleRowResultIcon = new ExampleRowResultIcon(featureName, scenarioName, exampleTableNumber);

        StringBuffer renderedTable = new StringBuffer();
        renderedTable.append(examples.getKeyword()).append(": ");
        if (examples.getName() != null) {
            renderedTable.append(examples.getName());
        }
        renderedTable.append("  ").append(System.lineSeparator());

        if (examples.getDescription() != null) {
            renderedTable.append(examples.getDescription()).append("  ").append(System.lineSeparator());
        }

        renderedTable.append(System.lineSeparator());
        addRow(renderedTable, examples.getTableHeader().getCells()," ");
        addSeparatorCells(renderedTable, examples.getTableHeader().getCells().size());

        for(TableRow row : examples.getTableBody()) {
            addRow(renderedTable, row.getCells(), exampleRowResultIcon.resultToken());
        }

        return renderedTable.toString();
    }


    private static String renderedExamples(Examples examples) {
        StringBuffer renderedTable = new StringBuffer();
        renderedTable.append(examples.getKeyword()).append(": ");
        if (examples.getName() != null) {
            renderedTable.append(examples.getName());
        }
        renderedTable.append("  ").append(System.lineSeparator());

        if (examples.getDescription() != null) {
            renderedTable.append(examples.getDescription()).append("  ").append(System.lineSeparator());
        }

        renderedTable.append(System.lineSeparator());
        addRow(renderedTable, examples.getTableHeader().getCells(), null);
        addSeparatorCells(renderedTable, examples.getTableHeader().getCells().size());

        for(TableRow row : examples.getTableBody()) {
            addRow(renderedTable, row.getCells(),null);
        }

        return renderedTable.toString();
    }

    private static String renderedDataTable(DataTable dataTable) {
        StringBuffer renderedTable = new StringBuffer();
        renderedTable.append(System.lineSeparator()).append(System.lineSeparator());
        TableRow header = dataTable.getRows().get(0);
        addRow(renderedTable, header.getCells());
        addSeparatorCells(renderedTable, header.getCells().size());

        for(int row = 1; row < dataTable.getRows().size(); row++) {
            addRow(renderedTable, dataTable.getRows().get(row).getCells());
        }
        return renderedTable.toString();
    }

    private static void addSeparatorCells(StringBuffer renderedTable, int columnCount) {
        renderedTable.append("|");
        for(int col = 0; col < columnCount; col ++) {
            renderedTable.append("-----------").append("|");
        }
        renderedTable.append(System.lineSeparator());
    }
    private static void addRow(StringBuffer renderedTable, List<TableCell> cells) {
        addRow(renderedTable, cells, null);
    }

    private static void addRow(StringBuffer renderedTable, List<TableCell> cells, String statusToken) {
        renderedTable.append("|");
        for(TableCell cell : cells) {
            renderedTable.append(cell.getValue()).append(" |");
        }
        if (statusToken != null) {
            renderedTable.append(statusToken + " |");
        }
        renderedTable.append(System.lineSeparator());
    }

    private static String withEscapedParameterFields(String text) {
        return text.replaceAll("<","{").replaceAll(">","}");
    }
}
