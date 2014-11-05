package net.thucydides.core.reports.html;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.regex.Pattern;

import static org.apache.commons.collections.IteratorUtils.toList;

public class ExampleTable {
    private static final String SQUARE_BRACKETS_OR_WHITE_SPACE = "[]［］ \t";
    List<String> headers;
    List<List<String>> rows = Lists.newArrayList();

    final static Pattern NEW_LINE = Pattern.compile("(\\r\\n)|(\\n)|(\\r)|(␤)|(\\r␤)");

    public ExampleTable(String tableContents) {
            List<String> lines = toList(Splitter.on(NEW_LINE)
                    .omitEmptyStrings()
                    .trimResults(CharMatcher.anyOf(SQUARE_BRACKETS_OR_WHITE_SPACE))
                    .split(tableContents).iterator());
            addHeaderFrom(lines.get(0));
            for(int row = 1; row < lines.size(); row++) {
                addRowFrom(lines.get(row));
            }
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
            line = line.substring(0,line.length() - 1);
        }

        return toList(Splitter.on("|").trimResults().split(line).iterator());
    }

    public String inHtmlFormat() {
        return "<table class='embedded'>" + getHtmlHeader() + getHtmlBody() + "</table>";
    }

    public String getHtmlHeader() {
        StringBuffer htmlHeader = new StringBuffer();
        htmlHeader.append("<thead>");
        for(String header : headers) {
            htmlHeader.append("<th>").append(header).append("</th>");
        }
        htmlHeader.append("</thead>");
        return htmlHeader.toString();
    }

    public String getHtmlBody() {
        StringBuffer htmlBody = new StringBuffer();
        htmlBody.append("<tbody>");
        for(List<String> row : rows) {
            htmlBody.append("<tr>");
            for(String cell : row) {
                htmlBody.append("<td>").append(cell).append("</td>");
            }
            htmlBody.append("</tr>");
        }
        htmlBody.append("</tbody>");
        return htmlBody.toString();
    }

}
