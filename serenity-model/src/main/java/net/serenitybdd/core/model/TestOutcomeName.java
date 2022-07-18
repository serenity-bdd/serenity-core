package net.serenitybdd.core.model;

import net.thucydides.core.model.TestOutcome;

public class TestOutcomeName {
    public static String from(TestOutcome testOutcome) {
        if (testOutcome == null) { return ""; }
        return testOutcome.getStoryTitle() + " - " + testOutcome.getTitle();
    }
}
