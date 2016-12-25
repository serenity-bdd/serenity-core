package net.thucydides.core.reports.html;

import com.github.rjeschke.txtmark.Processor;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Key;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.issues.IssueTracking;
import net.thucydides.core.reports.renderer.Asciidoc;
import net.thucydides.core.reports.renderer.MarkupRenderer;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.translate.AggregateTranslator;
import org.apache.commons.lang3.text.translate.CharSequenceTranslator;
import org.apache.commons.lang3.text.translate.EntityArrays;
import org.apache.commons.lang3.text.translate.LookupTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ch.lambdaj.Lambda.join;
import static net.thucydides.core.reports.html.MarkdownRendering.RenderedElements.*;
import static org.apache.commons.lang3.StringUtils.abbreviate;

/**
 * Format text for HTML reports.
 * In particular, this integrates JIRA links into the generated reports.
 */
public class Formatter {

    private final static String ISSUE_NUMBER_REGEXP = "#([A-Z][A-Z0-9-_]*)?-?\\d+";
    private final static Pattern shortIssueNumberPattern = Pattern.compile(ISSUE_NUMBER_REGEXP);
    private final static String FULL_ISSUE_NUMBER_REGEXP = "([A-Z][A-Z0-9-_]*)-\\d+";
    private final static Pattern fullIssueNumberPattern = Pattern.compile(FULL_ISSUE_NUMBER_REGEXP);
    private final static String ISSUE_LINK_FORMAT = "<a target=\"_blank\" href=\"{0}\">{1}</a>";
    private static final String ELIPSE = "&hellip;";
    private static final String ASCIIDOC = "asciidoc";
    private static final String MARKDOWN = "markdown";
    private static final String TEXT = "";
    private static final String NEW_LINE = "\n";

    private final static String NEWLINE_CHAR = "\u2424";
    private final static String NEWLINE = "\u0085";
    private final static String LINE_SEPARATOR = "\u2028";
    private final static String PARAGRAPH_SEPARATOR = "\u2029";

    private final static Logger LOGGER = LoggerFactory.getLogger(Formatter.class);

    private final IssueTracking issueTracking;
    private final EnvironmentVariables environmentVariables;
    private final MarkupRenderer asciidocRenderer;
//    private final Markdown4jProcessor markdown4jProcessor;

    @Inject
    public Formatter(IssueTracking issueTracking, EnvironmentVariables environmentVariables) {
        this.issueTracking = issueTracking;
        this.environmentVariables = environmentVariables;
        this.asciidocRenderer = Injectors.getInjector().getInstance(Key.get(MarkupRenderer.class, Asciidoc.class));
    }

    public Formatter(IssueTracking issueTracking) {
        this(issueTracking, Injectors.getInjector().getProvider(EnvironmentVariables.class).get() );
    }

    public String renderAsciidoc(String text) {
        return stripNewLines(asciidocRenderer.render(text));
    }

    public String renderMarkdown(String text) {
        if (text == null) { return ""; }
        return stripSurroundingParagraphTagsFrom(Processor.process(text));
    }

    private String stripSurroundingParagraphTagsFrom(String text) {
        if (text.toLowerCase().trim().startsWith("<p>")) {
            text = text.trim().substring(3);
        }

        if (text.toLowerCase().trim().endsWith("</p>")) {
            text = text.trim().substring(0, text.length() - 4);
        }
        return text;
    }

    private String stripNewLines(String render) {
        return render.replaceAll("\n", "");
    }

    public String stripQualifications(String title) {
        if (title == null) {
            return "";
        }
        if (title.contains("[")) {
            return title.substring(0,title.lastIndexOf("[")).trim();
        } else {
            return title;
        }
    }

    public String renderText(String text) {
        if (text == null) {
            return "";
        }
        return concatLines(BASIC_XML.translate(stringFormOf(text)),"<br>")
                .replaceAll("\\t", "&nbsp; &nbsp; &nbsp;");
    }

    public String renderHeaders(String text) {
        if (text == null) {
            return "";
        }
        return concatLines(BASIC_XML.translate(stringFormOf(text)),"<br>")
                .replaceAll("\\t", "");
    }

    static class IssueExtractor {
        private String workingCopy;

        IssueExtractor(String initialValue) {
            this.workingCopy = initialValue;
        }


        public List<String> getShortenedIssues() {
            Matcher matcher = shortIssueNumberPattern.matcher(workingCopy);

            ArrayList<String> issues = Lists.newArrayList();
            while (matcher.find()) {
                String issue = matcher.group();
                issues.add(issue);
                workingCopy = workingCopy.replaceFirst(issue, "");
            }

            return issues;
        }

        public List<String> getFullIssues() {
            Matcher unhashedMatcher = fullIssueNumberPattern.matcher(workingCopy);

            ArrayList<String> issues = Lists.newArrayList();
            while (unhashedMatcher.find()) {
                String issue = unhashedMatcher.group();
                issues.add(issue);
                workingCopy = workingCopy.replaceFirst(issue, "");
            }

            return issues;
        }

    }

    public static List<String> issuesIn(final String value) {

        IssueExtractor extractor = new IssueExtractor(value);

        List<String> issuesWithHash = extractor.getShortenedIssues();
        List<String> allIssues = extractor.getFullIssues();
        allIssues.addAll(issuesWithHash);

        return allIssues;
    }

    public String addLinks(final String value) {
        if (issueTracking == null) {
            return value;
        }
        String formattedValue = value;
        if (issueTracking.getIssueTrackerUrl() != null) {
            formattedValue = insertFullIssueTrackingUrls(value);
        }
        if (issueTracking.getShortenedIssueTrackerUrl() != null) {
            formattedValue = insertShortenedIssueTrackingUrls(formattedValue);
        }
        return formattedValue;
    }


    public String renderDescription(final String text) {
        String format = environmentVariables.getProperty(ThucydidesSystemProperty.NARRATIVE_FORMAT, TEXT);

        if (isRenderedHtml(text)) {
            return text;
        } else if (format.equalsIgnoreCase(ASCIIDOC)) {  // Use ASCIIDOC if configured
            return renderAsciidoc(text);
        } else if (format.equalsIgnoreCase(MARKDOWN) ||  (MarkdownRendering.configuredIn(environmentVariables).renderMarkdownFor(narrative)) ) {
            return renderMarkdown(text);
        } else {
            return addLineBreaks(text);
        }
    }

    private boolean isRenderedHtml(String text) {
        return (text != null) && (text.startsWith("<"));
    }

    public static String addLineBreaks(final String text) {
        return (text != null) ?
                text.replaceAll(IOUtils.LINE_SEPARATOR_WINDOWS, "<br>").replaceAll(IOUtils.LINE_SEPARATOR_UNIX, "<br>") : "";
    }

    public String convertAnyTables(String text) {
        if (shouldFormatEmbeddedTables() && containsEmbeddedTable(text)) {
            text = convertNonStandardNLChars(text);
            text = ExampleTable.stripBracketsFromOuterPipes(text);
            text = withTablesReplaced(text);

        }
        return text;
    }

    private String withTablesReplaced(String text) {
        List<String> unformattedTables = getEmbeddedTablesIn(text);
        for(String unformattedTable : unformattedTables) {
//            String unformattedTable = getFirstEmbeddedTable(text);
            ExampleTable table = new ExampleTable(unformattedTable);

            text = text.replace(unformattedTable, table.inHtmlFormat());
        }
        text = text.replaceAll(newLineUsedIn(text), "<br>");
        return text;
    }

    private String convertNonStandardNLChars(String text) {
        text = StringUtils.replace(text, NEWLINE_CHAR, NEW_LINE);
        text = StringUtils.replace(text, NEWLINE, NEW_LINE);
        text = StringUtils.replace(text, LINE_SEPARATOR, NEW_LINE);
        text = StringUtils.replace(text, PARAGRAPH_SEPARATOR, NEW_LINE);
        return text;
    }

    private boolean shouldFormatEmbeddedTables() {
        return !(ThucydidesSystemProperty.IGNORE_EMBEDDED_TABLES.booleanFrom(environmentVariables));
    }

    private boolean containsEmbeddedTable(String text) {
        return ((positionOfFirstPipeIn(text) >= 0)  && (positionOfLastPipeIn(text) >= 0));
    }

    private int positionOfLastPipeIn(String text) {
        return text.indexOf("|", positionOfFirstPipeIn(text) + 1);
    }

    private int positionOfFirstPipeIn(String text) {
        return text.indexOf("|");
    }

    private List<String> getEmbeddedTablesIn(String text) {
        List<String> embeddedTables = Lists.newArrayList();
        StringBuffer tableText = new StringBuffer();
        try (BufferedReader reader = new BufferedReader(new StringReader(text))) {
            boolean inTable = false;
            String newLine = newLineUsedIn(text);
            String line;
            while ((line = reader.readLine()) != null) {
                if (!inTable && line.contains("|")){ // start of a table
                    inTable = true;
                } else if (inTable && !line.contains("|") && !(isBlank(line))){ // end of a table
                    embeddedTables.add(tableText.toString().trim());
                    tableText = new StringBuffer();
                    inTable = false;
                }
                if (inTable) {
                    tableText.append(line).append(newLine);
                }
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not process embedded table", e);
        }

        if (!tableText.toString().isEmpty()) {
            embeddedTables.add(tableText.toString().trim());
        }
        return embeddedTables;

    }

    private boolean isBlank(String line) {
        return (StringUtils.isBlank(line.trim()));
    }

    private String newLineUsedIn(String text) {
        if (text.contains("\r\n")) {
            return "\r\n";
        } else if (text.contains("\n")) {
            return "\n";
        } else if (text.contains("\r")) {
            return "\r";
        } else {
            return NEW_LINE;
        }
    }

    private static final CharSequenceTranslator ESCAPE_SPECIAL_CHARS = new AggregateTranslator(
            new LookupTranslator(EntityArrays.ISO8859_1_ESCAPE()),
            new LookupTranslator(EntityArrays.HTML40_EXTENDED_ESCAPE())
    );

    private final CharSequenceTranslator BASIC_XML = new AggregateTranslator(
            new LookupTranslator(EntityArrays.BASIC_ESCAPE())
    );

    public String htmlCompatible(Object fieldValue) {
        return plainHtmlCompatible(fieldValue);
    }

    public String htmlCompatibleListEntry(Object fieldValue) {
        return (MarkdownRendering.configuredIn(environmentVariables).renderMarkdownFor(list)) ?
                renderMarkdown(htmlCompatible(fieldValue)) : htmlCompatible(fieldValue);
    }

    public String htmlCompatibleStoryTitle(Object fieldValue) {
        return (MarkdownRendering.configuredIn(environmentVariables).renderMarkdownFor(story)) ?
                renderMarkdown(htmlCompatible(fieldValue)) : htmlCompatible(fieldValue);
    }

    public String htmlCompatibleTestTitle(Object fieldValue) {
        return (MarkdownRendering.configuredIn(environmentVariables).renderMarkdownFor(scenario)) ?
                renderMarkdown(htmlCompatible(fieldValue)) : htmlCompatible(fieldValue);
    }

    public String plainHtmlCompatible(Object fieldValue) {
        return addLineBreaks(ESCAPE_SPECIAL_CHARS.translate(fieldValue != null ? stringFormOf(fieldValue) : "")).trim();
    }

    public String htmlAttributeCompatible(Object fieldValue) {
        if (fieldValue == null) { return ""; }

        return concatLines(ESCAPE_SPECIAL_CHARS.translate(stringFormOf(fieldValue)
                .replaceAll("<", "(")
                .replaceAll(">", ")")
                .replaceAll("\"", "'")));
    }

    public String htmlAttributeCompatible(Object fieldValue, int maxLength) {
        return abbreviate(htmlAttributeCompatible(fieldValue), maxLength);
    }

    public ResultIconFormatter resultIcon() {
        return new ResultIconFormatter();
    }

    public ResultRankingFormatter resultRank() {
        return new ResultRankingFormatter();
    }

    private static String concatLines(String message) {
        return concatLines(message," ");
    }

    private static String concatLines(String message, String replace) {
        String[] lines = message.replaceAll("\\r", "").split("\\n");
        return StringUtils.join(lines,replace);
    }

    private static String stringFormOf(Object fieldValue) {
        if (Iterable.class.isAssignableFrom(fieldValue.getClass())) {
            return "[" + join(fieldValue) + "]";
        } else {
            return fieldValue.toString();
        }
    }

    public String truncatedHtmlCompatible(String text, int length) {
        return renderMarkdown(addLineBreaks(ESCAPE_SPECIAL_CHARS.translate(truncate(text, length))));
    }

    private String truncate(String text, int length) {
        if (text.length() > length) {
            return text.substring(0, length).trim() + ELIPSE;
        } else {
            return text;
        }
    }


    private String replaceWithTokens(String value, List<String> issues) {
        List<String> sortedIssues = inOrderOfDecreasingLength(issues);
        for(int i = 0; i < sortedIssues.size(); i++) {
            value = value.replaceAll(sortedIssues.get(i), "%%%" + i  + "%%%");
        }
        return value;
    }

    private List<String> inOrderOfDecreasingLength(List<String> issues) {
        List<String> sortedIssues = Lists.newArrayList(issues);
        Collections.sort(sortedIssues, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o2.length() - o1.length();
            }
        });
        return sortedIssues;
    }

    public static List<String> shortenedIssuesIn(String value) {
        IssueExtractor extractor = new IssueExtractor(value);
        return extractor.getShortenedIssues();
    }

    public static List<String> fullIssuesIn(String value) {
        IssueExtractor extractor = new IssueExtractor(value);
        return extractor.getFullIssues();
    }


    private String insertShortenedIssueTrackingUrls(String value) {
        String issueUrlFormat = issueTracking.getShortenedIssueTrackerUrl();
        List<String> issues = sortByDecreasingSize(shortenedIssuesIn(value));
        String formattedValue = replaceWithTokens(value, issues);
        int i = 0;
        for (String issue : issues) {
            String issueUrl = MessageFormat.format(issueUrlFormat, stripLeadingHashFrom(issue));
            String issueLink = MessageFormat.format(ISSUE_LINK_FORMAT, issueUrl, issue);
            String token = "%%%" + i++ + "%%%";
            formattedValue = formattedValue.replaceAll(token, issueLink);
        }
        return formattedValue;
    }

    private String insertFullIssueTrackingUrls(String value) {
        String issueUrlFormat = issueTracking.getIssueTrackerUrl();
        List<String> issues = sortByDecreasingSize(fullIssuesIn(value));
        String formattedValue = replaceWithTokens(value, issues);
        int i = 0;
        for (String issue : issues) {
            String issueUrl = MessageFormat.format(issueUrlFormat, issue);
            String issueLink = MessageFormat.format(ISSUE_LINK_FORMAT, issueUrl, issue);
            String token = "%%%" + i++ + "%%%";
            formattedValue = formattedValue.replaceAll(token, issueLink);
        }
        return formattedValue;
    }

    private List<String> sortByDecreasingSize(List<String> issues) {
        List<String> sortedIssues = Lists.newArrayList(issues);
        Collections.sort(sortedIssues, new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                return Integer.valueOf(-a.length()).compareTo(Integer.valueOf(b.length()));
            }
        });
        return sortedIssues;
    }

    public String formatWithFields(String textToFormat) {
        String textWithEscapedFields = textToFormat.replaceAll("<", "&lt;").replaceAll(">", "&gt;");

        String renderedText = addLineBreaks(removeMacros(convertAnyTables(textWithEscapedFields)));
        if (MarkdownRendering.configuredIn(environmentVariables).renderMarkdownFor(step)) {
            renderedText = renderMarkdown(renderedText);
        }
        return renderedText;

    }

    private String removeMacros(String textToFormat) {
        return textToFormat.replaceAll("\\{trim=false\\}\\s*\\r?\\n","");
    }

    private String stripLeadingHashFrom(final String issue) {
        return issue.substring(1);
    }
}
