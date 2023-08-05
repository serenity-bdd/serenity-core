package net.thucydides.model.reports.templates;

/**
 * An error that occurs during template merging, such as a template parsing error.
 */
public class TemplateMergeException extends Exception {
    public TemplateMergeException(String message, Exception cause) {
        super(message,cause);
    }
}
