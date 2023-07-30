package net.thucydides.core.model.formatters;

import net.serenitybdd.core.collect.NewList;
import net.serenitybdd.core.di.ModelInfrastructure;
import net.thucydides.core.environment.SystemEnvironmentVariables;
import net.thucydides.core.issues.IssueTracking;
import net.thucydides.core.reports.html.ContextIconFormatter;
import net.thucydides.core.reports.html.ResultIconFormatter;
import net.thucydides.core.util.EnvironmentVariables;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReportFormatter {

    private final static String ISSUE_LINK_FORMAT = "<a target=\"_blank\" href=\"{0}\">{1}</a>";

    private final IssueTracking issueTracking;

    public ReportFormatter() {
        this(ModelInfrastructure.getIssueTracking());
    }

    public ReportFormatter(IssueTracking issueTracking) {
        this.issueTracking = issueTracking;
    }

    public static List<String> issuesIn(final String value) {
        IssueExtractor extractor = new IssueExtractor(value);
        List<String> issuesWithHash = extractor.getShortenedIssues();
        List<String> allIssues = extractor.getFullIssues();
        allIssues.addAll(issuesWithHash);
        return allIssues;
    }

    public String stripQualifications(String title){
        if (title == null) {
            return "";
        }
        if (title.contains("[")) {
            return title.substring(0,title.lastIndexOf("[")).trim();
        } else {
            return title;
        }

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

    public String asIssueLink(String issueNumber) {
        if (issueTracking == null || issueTracking.getIssueTrackerUrl() == null) {
            return issueNumber;
        } else {
            return issueTracking.getIssueTrackerUrl().replace("{0}",issueNumber);
        }
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
        List<String> sortedIssues = new ArrayList<>(issues);
        Collections.sort(sortedIssues, (a, b) -> Integer.valueOf(-a.length()).compareTo(Integer.valueOf(b.length())));
        return sortedIssues;
    }

    private String stripLeadingHashFrom(final String issue) {
        return issue.substring(1);
    }

    public static List<String> fullIssuesIn(String value) {
        IssueExtractor extractor = new IssueExtractor(value);
        return extractor.getFullIssues();
    }

    public static List<String> shortenedIssuesIn(String value) {
        IssueExtractor extractor = new IssueExtractor(value);
        return extractor.getShortenedIssues();
    }

    private String replaceWithTokens(String value, List<String> issues) {
        List<String> sortedIssues = inOrderOfDecreasingLength(issues);
        for(int i = 0; i < sortedIssues.size(); i++) {
            value = value.replaceAll(sortedIssues.get(i), "%%%" + i  + "%%%");
        }
        return value;
    }


    private List<String> inOrderOfDecreasingLength(List<String> issues) {
        List<String> sortedIssues = NewList.copyOf(issues);
        sortedIssues.sort((o1, o2) -> o2.length() - o1.length());
        return sortedIssues;
    }

    public ResultIconFormatter resultIcon() {
        return new ResultIconFormatter();
    }

    public ContextIconFormatter contextIcon() {
        return new ContextIconFormatter();
    }
}
