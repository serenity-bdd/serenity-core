package net.serenitybdd.model.model;

import net.serenitybdd.model.environment.ConfiguredEnvironment;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.domain.TestStep;
import net.thucydides.model.screenshots.ScreenshotAndHtmlSource;

import java.nio.file.Files;
import java.nio.file.Path;

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
        Path reportDirectory = ConfiguredEnvironment.getConfiguration().getOutputDirectory().toPath();
        return (pageSourceLink != null) && (Files.exists(reportDirectory.resolve(pageSourceLink)));
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
