package net.serenitybdd.model.model;

import net.thucydides.model.domain.TestOutcome;

public class TestOutcomeName {
    public static String from(TestOutcome testOutcome) {
        if (testOutcome == null) { return ""; }
        return testOutcome.getStoryTitle() + " - " + testOutcome.getTitle();
    }
}
