package net.thucydides.core.reports;

/**
 * Created by john on 22/09/2014.
 */
public class TestOutcomesFailures extends AssertionError {

    public TestOutcomesFailures(String message) {
        super(message);
    }
}
