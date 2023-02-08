package net.serenitybdd.plugins.jira.model;

public class JQLException extends RuntimeException{

    public JQLException(String message) {
        super(message);
    }

    public JQLException(Throwable throwable) {
        super(throwable);
    }
}
