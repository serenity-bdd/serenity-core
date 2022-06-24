package net.serenitybdd.lambdatest;

import net.thucydides.core.model.TestOutcome;

public class LambdaTestName {
    public static String from(TestOutcome testOutcome) {
        return testOutcome.getStoryTitle() + " - " + testOutcome.getTitle();
    }
}
