package net.serenitybdd.core.model;

import net.serenitybdd.core.environment.ConfiguredEnvironment;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestStep;
import net.thucydides.core.screenshots.ScreenshotAndHtmlSource;

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
