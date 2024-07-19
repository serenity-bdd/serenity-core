package net.thucydides.model.requirements.reports;

import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RenderMarkdown {

    private final String originalText;
    private final List<String> lines;


    private final static Pattern DATA_TABLE_LINE = Pattern.compile("\\s*(\\[|［)?(\\|.*\\|)(\\]|］?)\\s*");//"\\s*\\|.*\\|\\s*");
    private final static Pattern SEPARATOR_LINE = Pattern.compile("\\|(-|\\s|\\|)+\\|");


    private RenderMarkdown(String text) {
        this.originalText = text;
        lines = Arrays.asList(text.split("\\r?\\n"));
    }

    public static String convertEmbeddedTablesIn(String text) {
        return new RenderMarkdown(text).convertTables();
    }

    public static String preprocessMarkdownTables(String text) {
        return new RenderMarkdown(text).injectNewLineBeforeTables();
    }

    private String injectNewLineBeforeTables() {
        List<NarrativeBlock> blocks = convertToBlocks(lines);
        List<String> spacedLines = new ArrayList<>();

        for(NarrativeBlock block: blocks) {
            if (block.isTable()) {
                spacedLines.add(System.lineSeparator());
                spacedLines.addAll(block.lines);
            } else {
                spacedLines.addAll(block.lines);
            }
        }

        return spacedLines.stream().collect(Collectors.joining(System.lineSeparator()));
    }

    private String convertTables() {
        if (noTablularLinesIn(lines)) {
            return originalText;
        }
        if (isRenderedMarkdownTableIn(tabularLinesIn(lines))) {
            return originalText;
        }
        return renderMarkdownTable();
    }

    private String renderMarkdownTable() {

        List<NarrativeBlock> blocks = convertToBlocks(lines);

        List<String> markdownTable = new ArrayList<>();
        blocks.forEach(
                block -> markdownTable.addAll(block.asMarkdown())
        );

        return markdownTable.stream().collect(Collectors.joining(System.lineSeparator())) + System.lineSeparator();
    }

    private List<NarrativeBlock> convertToBlocks(List<String> lines) {
        List<NarrativeBlock> blocks = new ArrayList<>();

        boolean inTable = false;

        NarrativeBlock currentBlock = NarrativeBlock.forNormalText();

        for (String line : lines) {
            if (switchingToATableSection(inTable, line)) {
                inTable = true;
                blocks.add(currentBlock);
                currentBlock = NarrativeBlock.forATable();
            } else if (switchingToATextSection(inTable, line)) {
                inTable = false;
                blocks.add(currentBlock);
                currentBlock = NarrativeBlock.forNormalText();
            }
            currentBlock.add(trimSquareBracketsFrom(line));
        }
        blocks.add(currentBlock);

        return blocks;
    }

    private boolean switchingToATableSection(boolean inTable, String line) {
        return !inTable && isDataTableLine(line);
    }

    private boolean switchingToATextSection(boolean inTable, String line) {
        return inTable && !isDataTableLine(line);
    }

    static int numberOfColumnsIn(List<String> tableLines) {
        return Splitter.on("|").omitEmptyStrings().splitToList(tableLines.get(0)).size();
    }

    private List<String> tabularLinesIn(List<String> lines) {
        List<String> tableLines = new ArrayList<>();

        int row = lines.size() - 1;
        String line = lines.get(row);
        while (isDataTableLine(line) && row >= 0) {
            tableLines.add(trimSquareBracketsFrom(line));
            row--;
            line = (row >= 0) ? lines.get(row) : "";
        }


        Collections.reverse(tableLines);
        return tableLines;
    }

    private String trimSquareBracketsFrom(String line) {
        Matcher tableLine = DATA_TABLE_LINE.matcher(line);
        if (tableLine.matches()) {
            return tableLine.group(2);
        } else {
            return line;
        }
    }

    private boolean isRenderedMarkdownTableIn(List<String> lines) {
        return lines.stream().anyMatch(this::isSeparatorLine);
    }

    private boolean noTablularLinesIn(List<String> lines) {
        return lines.stream().noneMatch(this::isDataTableLine);
    }

    private boolean isDataTableLine(String line) {
        return DATA_TABLE_LINE.matcher(line).matches();
    }

    private boolean isSeparatorLine(String line) {
        return SEPARATOR_LINE.matcher(line).matches();
    }


    private static class NarrativeBlock {

        public static NarrativeBlock forNormalText() {
            return new NarrativeBlock(false);
        }

        public static NarrativeBlock forATable() {
            return new NarrativeBlock(true);
        }

        private final boolean isTable;

        private List<String> lines = new ArrayList<>();

        private NarrativeBlock(boolean isTable) {
            this.isTable = isTable;
        }

        public void add(String line) {
            lines.add(line);
        }

        public List<String> getLines() {
            return lines;
        }

        public boolean isTable() {
            return isTable;
        }

        public List<String>  asMarkdown() {
            if (!isTable) {
                return lines;
            } else {
                return markdownTable();
            }
        }

        private List<String> markdownTable() {
            int columnCount = numberOfColumnsIn(lines);
            String separatorLine = "|" + StringUtils.repeat("---|", columnCount);

            List<String> markdownTable = new ArrayList<>();

            markdownTable.add(System.lineSeparator());
            markdownTable.add(lines.get(0));
            markdownTable.add(separatorLine);
            for(int row = 1; row < lines.size(); row++) {
                markdownTable.add(lines.get(row));
            }

            return markdownTable;
        }
    }

}
