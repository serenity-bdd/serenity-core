package net.serenitybdd.plugins.jira.service;

/**
 * No matching issue was found in the issue tracking system.
 */
public class NoSuchIssueException extends RuntimeException {
    public NoSuchIssueException(String message) {
        super(message);
    }
}
