package net.serenitybdd.core.model;

import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestStep;
import net.thucydides.core.screenshots.ScreenshotAndHtmlSource;

public class FailureDetails {
    private final TestOutcome testOutcome;

    public FailureDetails(TestOutcome testOutcome) {
        this.testOutcome = testOutcome;
    }

    public String getConciseErrorMessage() {
        if (testOutcome.firstStepWithErrorMessage().isPresent()) {
            return testOutcome.firstStepWithErrorMessage().get().getConciseErrorMessage();
        }
        return testOutcome.testFailureMessage().orElse("");
    }

    public String getCompleteErrorMessage() {
        if (testOutcome.firstStepWithErrorMessage().isPresent()) {
            return testOutcome.firstStepWithErrorMessage().get().getErrorMessage();
        }
        return testOutcome.testFailureMessage().orElse("");
    }

    public String getPageSourceLink() {
        for(TestStep testStep : testOutcome.getFlattenedTestSteps()) {
            for(ScreenshotAndHtmlSource screenshot : testStep.getScreenshots()) {
                if (screenshot.getHtmlSourceName() != null) {
                    return screenshot.getHtmlSourceName();
                }
            }
        }
        return "#";
    }
}
