package serenitymodel.net.thucydides.core.issues;

/**
 * Determine the issue tracking URL formats for a project.
 */
public interface IssueTracking {

    String getIssueTrackerUrl();
    String getShortenedIssueTrackerUrl();

}
