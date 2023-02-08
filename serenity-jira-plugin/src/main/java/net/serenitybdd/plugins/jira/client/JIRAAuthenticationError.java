package net.serenitybdd.plugins.jira.client;

/**
 * A description goes here.
 * User: john
 * Date: 7/03/2014
 * Time: 2:39 PM
 */
public class JIRAAuthenticationError extends RuntimeException {
    public JIRAAuthenticationError(String message) {
        super(message);
    }
}
