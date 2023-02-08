package net.serenitybdd.plugins.jira.service;


import com.google.inject.Inject;
import net.serenitybdd.plugins.jira.domain.IssueComment;
import net.serenitybdd.plugins.jira.domain.IssueSummary;
import net.serenitybdd.plugins.jira.domain.IssueTransition;
import net.serenitybdd.plugins.jira.model.IssueTracker;
import net.serenitybdd.plugins.jira.model.IssueTrackerUpdateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Update comments in JIRA issues with links to Thucydides reports.
 * This plugin will use the JIRA username and password provided in the <b>jira.username</b>
 * and <b>jira.password</b> system properties. The URL of the JIRA instance should be provided
 * using the <b>jira.url</b> system property.
 */
public class JiraIssueTracker implements IssueTracker {

    private final Logger logger;
    private final JIRAConnection jiraConnection;
    private final Marker warn = MarkerFactory.getMarker("WARN");

    @Inject
    public JiraIssueTracker(JIRAConfiguration jiraConfiguration) {
        this(LoggerFactory.getLogger(JiraIssueTracker.class), jiraConfiguration);
    }

    public JiraIssueTracker(Logger logger, JIRAConfiguration jiraConfiguration) {
        this.logger = logger;
        this.jiraConnection = new JIRAConnection(jiraConfiguration);
    }
    
    public JIRAConnection getJiraConnection() {
    	return this.jiraConnection;
    }

    @Override
    public String toString() {
        return "Connection to JIRA instance at " + jiraConnection.getJiraWebserviceUrl()
                + " with user " + jiraConnection.getJiraUser();
    }

    /**
     * Add a comment to the specified issue.
     * The author is the JIRA user specified in the *jira.user* system property.
     *
     * @param issueKey the unique key identifying the issue to be commented.
     * @param commentText  text of the comment.
     * @throws IssueTrackerUpdateException if something wrong
     */
    public void addComment(final String issueKey, final String commentText) {
            jiraConnection.getRestJiraClient().addComment(issueKey,new IssueComment(commentText));
    }


    private void logJiraIssueNotFound(String issueKey) {
        logger.error("No JIRA issue found with key {}", issueKey);
    }

    private void processJiraException(String issueKey, IOException exception) {
        if (noSuchIssue(exception)) {
            logger.error("No JIRA issue found with key {}", issueKey);
        } else {
            throw new IssueTrackerUpdateException("Could not update JIRA using URL ("
                                                  + jiraConnection.getJiraWebserviceUrl() + ")", exception);
        }
    }

    private boolean noSuchIssue(Exception exception) {
        return (exception.toString().contains("This issue does not exist"));
    }

    /**
     * Return the comments associated with the specified issue.
     *
     * @param issueKey Identifies the specified issue.
     * @return the list of comments.
     * @throws IssueTrackerUpdateException if something wrong
     */
    public List<IssueComment> getCommentsFor(String issueKey) throws IssueTrackerUpdateException {
        try {
            return jiraConnection.getRestJiraClient().getComments(issueKey);
        } catch (ParseException pe) {
            throw new IssueTrackerUpdateException(pe.getMessage(),pe);
        }
    }

    public void updateComment(String issuekey,IssueComment issueComment) {
         jiraConnection.getRestJiraClient().updateComment(issuekey,issueComment);
    }

    /**
     * Return the current status for a given JIRA issue.
     * Note that the status value depends on the issue workflow, so can be very variable.
     */
    public String getStatusFor(final String issueKey) throws IssueTrackerUpdateException {
        Optional<IssueSummary> issue = jiraConnection.getRestJiraClient().loadByKey(issueKey);
        if(issue.isPresent()) {
            return issue.get().getStatus();
        } else {
            logJiraIssueNotFound(issueKey);
            throw new IssueTrackerUpdateException("Issue not found " + issueKey, new NoSuchIssueException(issueKey));
        }
    }

    public void doTransition(final String issueKey, final String workflowAction) throws IssueTrackerUpdateException {
        try {
            Optional<IssueSummary> issue = jiraConnection.getRestJiraClient().loadByKey(issueKey);
            if(issue.isPresent()) {
                String actionId = getAvailableActions(issueKey).get(workflowAction.toLowerCase());
                if (actionId != null) {
                    jiraConnection.getRestJiraClient().progressWorkflowTransition(issueKey, actionId);
                }
            } else {
                logJiraIssueNotFound(issueKey);
                throw new IssueTrackerUpdateException("Issue not found " + issueKey, new NoSuchIssueException(issueKey));
            }
        } catch (ParseException pe) {
            throw new IssueTrackerUpdateException(pe.getMessage(),pe);
        }
    }


    private Map<String, String> getAvailableActions(final String issueKey) throws  ParseException {
        Map<String, String> availableActionMap = new HashMap<String, String>();
        List<IssueTransition> actions = jiraConnection.getRestJiraClient().getAvailableTransitions(issueKey);
        for(IssueTransition action : actions) {
            availableActionMap.put(action.getName().toLowerCase(), action.getId());
        }
        return availableActionMap;
    }

    private Map<String, String> statusCodeMap = null;
//    private Map<String, String> getStatusCodeMap() {
//        if (statusCodeMap == null) {
//            statusCodeMap = new HashMap<String, String>();
//            try {
//                String token = jiraConnection.getAuthenticationToken();
//                RemoteStatus[] statuses = jiraConnection.getJiraSoapService().getStatuses(token);
//                for(RemoteStatus status : statuses) {
//                    statusCodeMap.put(status.getId(), status.getName());
//                }
//            } catch (IOException e) {
//                throw new IssueTrackerUpdateException("Could not read JIRA using URL ("
//                                                      + jiraConnection.getJiraWebserviceUrl() + ")", e);
//            }
//        }
//        return statusCodeMap;
//    }


    private Map<String, String> statusLabelMap = null;
//    private String getStatusId(final String statusLabel) {
//        return getStatusLabelMap().get(statusLabel);
//    }

//    private Map<String, String> getStatusLabelMap() {
//        if (statusLabelMap == null) {
//            statusLabelMap = new HashMap<String, String>();
//            try {
//                String token = jiraConnection.getAuthenticationToken();
//                RemoteStatus[] statuses = jiraConnection.getJiraSoapService().getStatuses(token);
//                for(RemoteStatus status : statuses) {
//                    statusLabelMap.put(status.getName(), status.getId());
//                }
//            } catch (IOException e) {
//                throw new IssueTrackerUpdateException("Could not read JIRA using URL ("
//                                                      + jiraConnection.getJiraWebserviceUrl() + ")", e);
//            }
//        }
//        return statusLabelMap;
//    }

//    private void checkThatIssueExists(final RemoteIssue issue, final String issueKey) {
//        if (issue == null) {
//            logger.error(warn, "JIRA issue not found for {}",issueKey);
//            throw new NoSuchIssueException("No issue found for " + issueKey);
//        }
//    }
//
//    private RemoteComment newCommentWithText(final String commentText) {
//        RemoteComment comment = new RemoteComment();
//        comment.setAuthor(jiraConnection.getJiraUser());
//        comment.setBody(commentText);
//        return comment;
//    }
//
//    private class CommentConverter implements Converter<RemoteComment, IssueComment> {
//
//        public IssueComment convert(RemoteComment from) {
//            return new IssueComment(Long.valueOf(from.getId()), from.getBody(), from.getAuthor());
//        }
//    }
}
