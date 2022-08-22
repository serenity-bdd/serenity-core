package net.serenitybdd.core.model;

import net.serenitybdd.core.reporting.ReportDirectories;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestStep;
import net.thucydides.core.screenshots.ScreenshotAndHtmlSource;

import java.nio.file.Files;
import java.nio.file.Paths;

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

    public boolean pageSourceExists() {
        String pageSourceLink = getPageSourceLink();
        ReportDirectories reportDirectories = new ReportDirectories();
        return (pageSourceLink != null) && (Files.exists(reportDirectories.getReportDirectory().resolve(pageSourceLink)));
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
