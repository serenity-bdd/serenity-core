package net.thucydides.model.issues;

/**
 * Determine the issue tracking URL formats for a project.
 */
public interface IssueTracking {

    String getIssueTrackerUrl();
    String getShortenedIssueTrackerUrl();

}
