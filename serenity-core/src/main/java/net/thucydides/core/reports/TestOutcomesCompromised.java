package net.thucydides.core.reports;

/**
 * Created by john on 10/01/2016.
 */
public class TestOutcomesCompromised extends AssertionError {
    public TestOutcomesCompromised(String message) {
        super(message);
    }
}
