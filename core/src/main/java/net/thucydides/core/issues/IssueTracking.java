package net.thucydides.core.issues;

/**
 * Determine the issue tracking URL formats for a project.
 */
public interface IssueTracking {

    public String getIssueTrackerUrl();
    public String getShortenedIssueTrackerUrl();

}
