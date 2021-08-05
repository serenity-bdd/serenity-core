package net.thucydides.core.reports.json;

/**
 * Created by john on 4/07/2014.
 */
public class FailingStep {

    public Exception failsWithMessage(String message) {
        return new IllegalArgumentException(message);
    }

}
