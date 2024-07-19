package net.thucydides.model.domain.formatters;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IssueExtractor {

    private final static String ISSUE_NUMBER_REGEXP = "#([A-Z][A-Z0-9-_]*)?-?\\d+";
    private final static String FULL_ISSUE_NUMBER_REGEXP = "([A-Z][A-Z0-9-_]*)-\\d+";
    private String workingCopy;
    private final static Pattern shortIssueNumberPattern = Pattern.compile(ISSUE_NUMBER_REGEXP);
    private final static Pattern fullIssueNumberPattern = Pattern.compile(FULL_ISSUE_NUMBER_REGEXP);


    public IssueExtractor(String initialValue) {
        this.workingCopy = initialValue;
    }


    public List<String> getShortenedIssues() {
        Matcher matcher = shortIssueNumberPattern.matcher(workingCopy);

        ArrayList<String> issues = new ArrayList<>();
        while (matcher.find()) {
            String issue = matcher.group();
            issues.add(issue);
            workingCopy = workingCopy.replaceFirst(issue, "");
        }

        return issues;
    }

    public List<String> getFullIssues() {
        Matcher unhashedMatcher = fullIssueNumberPattern.matcher(workingCopy);

        ArrayList<String> issues = new ArrayList<>();
        while (unhashedMatcher.find()) {
            String issue = unhashedMatcher.group();
            issues.add(issue);
            workingCopy = workingCopy.replaceFirst(issue, "");
        }

        return issues;
    }

}
