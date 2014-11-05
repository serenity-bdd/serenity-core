package net.thucydides.core.reports.html;

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
    private static final String NEW_LINE = System.getProperty("line.separator");

    private final IssueTracking issueTracking;
    private final EnvironmentVariables environmentVariables;
    private final MarkupRenderer asciidocRenderer;

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

    private String stripNewLines(String render) {
        return render.replaceAll("\n","");
    }

    public String stripQualifications(String title) {
        if (title.contains("[")) {
            return title.substring(0,title.lastIndexOf("["));
        } else {
            return title;
        }
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
        String format = environmentVariables.getProperty(ThucydidesSystemProperty.NARRATIVE_FORMAT,"");
        if (isRenderedHtml(text)) {
            return text;
        } else if (format.equalsIgnoreCase(ASCIIDOC)) {
            return renderAsciidoc(text);
        } else {
            return addLineBreaks(text);
        }
    }

    private boolean isRenderedHtml(String text) {
        return (text != null) && (text.startsWith("<"));
    }

    public String addLineBreaks(final String text) {
        return (text != null) ?
                text.replaceAll(IOUtils.LINE_SEPARATOR_WINDOWS, "<br>").replaceAll(IOUtils.LINE_SEPARATOR_UNIX, "<br>") : "";
    }

    public String convertAnyTables(String text) {
        text = convertNonStandardNLChars(text);
        if (shouldFormatEmbeddedTables() && containsEmbeddedTable(text)) {
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
        text = StringUtils.replace(text, "\r␤", NEW_LINE);
        return StringUtils.replace(text, "␤", NEW_LINE);
    }

    private boolean shouldFormatEmbeddedTables() {
        return !environmentVariables.getPropertyAsBoolean(ThucydidesSystemProperty.IGNORE_EMBEDDED_TABLES, false);
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
        BufferedReader reader = new BufferedReader(new StringReader(text));
        StringBuffer tableText = new StringBuffer();
        boolean inTable = false;
        String newLine = newLineUsedIn(text);
        try {
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

    private String getFirstEmbeddedTable(String text) {
        BufferedReader reader = new BufferedReader(new StringReader(text));
        StringBuffer tableText = new StringBuffer();
        boolean inTable = false;
        String newLine = newLineUsedIn(text);
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!inTable && line.contains("|")){
                    inTable = true;
                } else if (inTable && !line.contains("|") && !(isBlank(line))){
                    break;
                }
                if (inTable) {
                    tableText.append(line).append(newLine);
                }
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not process embedded table", e);
        }
        return tableText.toString().trim();
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

    private final CharSequenceTranslator ESCAPE_SPECIAL_CHARS = new AggregateTranslator(
            new LookupTranslator(EntityArrays.ISO8859_1_ESCAPE()),
            new LookupTranslator(EntityArrays.HTML40_EXTENDED_ESCAPE())
    );

    public String htmlCompatible(Object fieldValue) {
        return addLineBreaks(ESCAPE_SPECIAL_CHARS.translate(fieldValue != null ? stringFormOf(fieldValue) : ""));
    }

    private String stringFormOf(Object fieldValue) {
        if (Iterable.class.isAssignableFrom(fieldValue.getClass())) {
            return "[" + join(fieldValue) + "]";
        } else {
            return fieldValue.toString();
        }
    }

    public String truncatedHtmlCompatible(String text, int length) {
        return addLineBreaks(ESCAPE_SPECIAL_CHARS.translate(truncate(text, length)));
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
                return new Integer(-a.length()).compareTo(new Integer(b.length()));
            }
        });
        return sortedIssues;
    }

    public String formatWithFields(String textToFormat, List<String> fields) {
        String textWithEscapedFields = textToFormat;
        for (String field : fields) {
            textWithEscapedFields = textWithEscapedFields.replaceAll("<" + field + ">", "&lt;" + field + "&gt;");
        }
        return addLineBreaks(removeMacros(convertAnyTables(textWithEscapedFields)));
    }

    private String removeMacros(String textToFormat) {
        return textToFormat.replaceAll("\\{trim=false\\}\\s*\\r?\\n","");
    }

    private String stripLeadingHashFrom(final String issue) {
        return issue.substring(1);
    }
}
