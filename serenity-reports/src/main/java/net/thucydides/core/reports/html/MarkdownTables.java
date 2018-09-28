package net.thucydides.core.reports.html;

import org.apache.commons.lang3.StringUtils;

public class MarkdownTables {

    public static String convertTablesIn(String text) {
        if (text == null || !text.contains("［|")) { return text; }

        String preamble = text.substring(0, text.indexOf("［|"));
        String tableText = text.substring(preamble.length());

        tableText = tableText.replaceAll("［","").replaceAll("］","");

        String[] lines = tableText.split("\\r?\\n");
        String header = lines[0];
        int columns = header.split("\\|").length - 1;
        String separatorLine = StringUtils.repeat("| --- ", columns) + "|";

        StringBuffer formatterTable = new StringBuffer(preamble);
        formatterTable.append(System.lineSeparator()).append(header).append("  ").append(System.lineSeparator());
        formatterTable.append(separatorLine).append("  ").append(System.lineSeparator());
        for(int row = 1; row < lines.length; row++) {
            formatterTable.append(lines[row]).append("  ").append(System.lineSeparator());
        }
        return formatterTable.toString();
    }
}
