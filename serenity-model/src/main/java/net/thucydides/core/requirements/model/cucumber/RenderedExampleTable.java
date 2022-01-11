package net.thucydides.core.requirements.model.cucumber;


import io.cucumber.messages.types.Examples;
import io.cucumber.messages.types.TableCell;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.System.lineSeparator;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class RenderedExampleTable {

    public static String nameFor(Examples exampleTable) {
        return emptyStringOrValueOf(exampleTable.getName());
    }

    public static String descriptionFor(Examples exampleTable) {
        return emptyStringOrValueOf(exampleTable.getDescription());
    }

    private static String emptyStringOrValueOf(String description) {
        if (isEmpty(description)) {
            return "";
        }

        return description + lineSeparator();
    }

    public static String renderedTable(Examples exampleTable, ExampleRowResultIcon exampleRowIcon) {

        final Map<Integer, Integer> maxColumnWidths = maxColumnWidthForColumnsIn(exampleTable);

        String headings = cellRow(exampleTable.getTableHeader().getCells(),
                                  maxColumnWidths,
                                  exampleTable.getLocation().getLine(),
                                  exampleRowIcon.noIcon())
                          + headerSeparator(maxColumnWidths);

        String body = exampleTable.getTableBody().stream()
                .map(row -> cellRow(row.getCells(), maxColumnWidths, row.getLocation().getLine(), exampleRowIcon))
                .collect(Collectors.joining());

        return headings + body;
    }

    private static String headerSeparator(Map<Integer, Integer> maxColumnWidths) {
        StringBuilder headerSeparator = new StringBuilder("|");
        for(int column = 0; column < maxColumnWidths.size(); column ++) {
            headerSeparator.append(StringUtils.repeat("-", maxColumnWidths.get(column) + 2)).append("|");
        }
        return headerSeparator + "---|" + lineSeparator();
    }

    public static String cellRow(List<TableCell> cells,
                                 Map<Integer, Integer> maxColumnWidths,
                                 long lineNumber,
                                 RowResultIcon exampleRowResultIcons) {

        StringBuilder headerRow = new StringBuilder("|");

        for(int column = 0; column < cells.size(); column ++) {
            String columnHeading = cells.get(column).getValue();
            int columnWidth = maxColumnWidths.get(column) + 1;
            headerRow.append(StringUtils.rightPad(" " + columnHeading, columnWidth)).append(" |");
        }
        headerRow.append(exampleRowResultIcons.resultToken(lineNumber)).append("|");
        return headerRow + lineSeparator();
    }

    private static Map<Integer, Integer> maxColumnWidthForColumnsIn(Examples exampleTable) {

        Map<Integer, Integer> maxColumnWidth = new HashMap<>();

        int columnCount = exampleTable.getTableHeader().getCells().size();
        for(int column = 0; column < columnCount; column++) {
            maxColumnWidth.put(column, maxColumnWidthFor(exampleTable, column));
        }
        return maxColumnWidth;
    }

    private static Integer maxColumnWidthFor(Examples exampleTable, int column) {
        int headerWidth = exampleTable.getTableHeader().getCells().get(column).getValue().length();

        int maxCellWidth = exampleTable.getTableBody()
                .stream()
                .mapToInt(row -> row.getCells().get(column).getValue().length())
                .max()
                .orElse(0);

        return Math.max(headerWidth, maxCellWidth);
    }
}
