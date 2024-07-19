package net.thucydides.model.reports.html;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DescriptionSplitter {
    private final List<String> lines;

    private final static Pattern DATA_TABLE_LINE = Pattern.compile("\\|.*\\|");

    private DescriptionSplitter(String text) {
        lines = Arrays.asList(text.split("\\r?\\n"));
    }

    public static List<String> splitIntoSteps(String statement) {
        return new DescriptionSplitter(statement).split();
    }

    private List<String> split() {

        List<String> blocks = new ArrayList<>();
        TableBuffer currentTable = new TableBuffer();

        for (String line : lines) {
            if (!isDataTableLine(line)) {
                currentTable.appendToPreviousLine(blocks);
                blocks.add(line);
            } else {
                currentTable.add(line);
            }
        }
        currentTable.appendToPreviousLine(blocks);

        return blocks;
    }

    private boolean isDataTableLine(String line) {
        return DATA_TABLE_LINE.matcher(line).matches();
    }

    private static class TableBuffer {

        private List<String> tableContents = new ArrayList<>();

        public void add(String line) {
            tableContents.add(line);
        }

        public String asString() {
            return tableContents.stream().collect(Collectors.joining(System.lineSeparator()));
        }

        public void appendToPreviousLine(List<String> lines) {
            if (!tableContents.isEmpty()) {
                lines.set(lines.size() - 1, lines.get(lines.size() - 1) + System.lineSeparator() + this.asString());
            }
            tableContents.clear();
        }
    }
}
