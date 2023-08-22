package net.thucydides.model.reports.html;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;
import static org.apache.commons.collections4.IteratorUtils.toList;

public class ExampleTable {

    private final static String NEWLINE_CHAR = "\u2424";
    private final static String NEWLINE = "\u0085";
    private final static String LINE_SEPARATOR = "\u2028";
    private final static String PARAGRAPH_SEPARATOR = "\u2089";
    private final static String LEFT_BRACKET = "\u0FF3B";
    private final static String RIGHT_BRACKET = "\u0FF3D";

    List<String> headers;
    List<List<String>> rows = new ArrayList<>();
    final static Pattern NEW_LINE
            = Pattern.compile("(\\r\\n)|(\\n)|(\\r)|(" + NEWLINE_CHAR + ")|(" + LINE_SEPARATOR + ")|(" + PARAGRAPH_SEPARATOR + ")|(\\r" + NEWLINE_CHAR + ")");
    private static final String SQUARE_BRACKETS_OR_WHITE_SPACE = "[]" + LEFT_BRACKET + RIGHT_BRACKET + "\t";

    public ExampleTable(String tableContents) {
        tableContents = stripBracketsFromOuterPipes(tableContents);
        List<String> lines = toList(Splitter.on(NEW_LINE)
                .omitEmptyStrings()
                .trimResults(CharMatcher.anyOf(SQUARE_BRACKETS_OR_WHITE_SPACE))
                .split(tableContents).iterator());
        addHeaderFrom(lines.get(0));
        for (int row = 1; row < lines.size(); row++) {
            addRowFrom(lines.get(row));
        }
    }

    public static String stripBracketsFromOuterPipes(String text) {
        text = StringUtils.replace(text, "[|", "|");
        text = StringUtils.replace(text, "［|", "|");
        text = StringUtils.replace(text, "|]", "|");
        text = StringUtils.replace(text, "|］", "|");
        text = StringUtils.replace(text, LEFT_BRACKET + "|", "|");
        text = StringUtils.replace(text, "|" + RIGHT_BRACKET, "|");
        return text;
    }

    private void addRowFrom(String row) {
        rows.add(cellsFrom(row));
    }

    private void addHeaderFrom(String headerLine) {
        headers = cellsFrom(headerLine);
    }

    private List<String> cellsFrom(String line) {
        line = line.trim();
        if (line.startsWith("|")) {
            line = line.substring(1);
        }
        if (line.endsWith("|")) {
            line = line.substring(0, line.length() - 1);
        }

        return toList(Splitter.on("|").trimResults().split(line).iterator());
    }

    public String inHtmlFormat() {
        if (rows.size() == 0) {
            return singleRowTable();
        } else if (headers.size() == 1) {
            return tabularList();
        } else {
            return tableWithHeaderAndRows();
        }
    }

    private String singleRowTable() {
        return "<table class='embedded'>" + getHtmlBody(asList(headers)) + "</table>";
    }

    private String tabularList() {
        List<List<String>> allRows = new ArrayList<>();
        allRows.add(headers);
        allRows.addAll(rows);
        return "<table class='embedded'>" + getHtmlBody(allRows) + "</table>";
    }

    private String tableWithHeaderAndRows() {
        return "<table class='embedded'>" + getHtmlHeader() + getHtmlBody(rows) + "</table>";
    }

    public String getHtmlHeader() {
        StringBuilder htmlHeader = new StringBuilder();
        String headerElement = (headers.size() > 1) ? "th" : "td";
        htmlHeader.append("<thead>");
        for (String header : headers) {
            htmlHeader.append("<").append(headerElement).append(">").append(header).append("</" + headerElement + ">");
        }
        htmlHeader.append("</thead>");
        return htmlHeader.toString();
    }

    public String getHtmlBody(List<List<String>> rows) {
        StringBuffer htmlBody = new StringBuffer();
        htmlBody.append("<tbody>");
        for (List<String> row : rows) {
            htmlBody.append("<tr>");
            for (String cell : row) {
                htmlBody.append("<td>").append(cell).append("</td>");
            }
            htmlBody.append("</tr>");
        }
        htmlBody.append("</tbody>");
        return htmlBody.toString();
    }

}
