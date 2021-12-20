package io.cucumber.core.plugin;

import io.cucumber.messages.types.Tag;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.steps.BaseStepListener;
import net.thucydides.core.util.EnvironmentVariables;

import java.util.List;
import java.util.Optional;

public class UpdateManualScenario {

    private BaseStepListener baseStepListener;
    private ManualScenarioChecker manualScenarioChecker;
    private String scenarioDescription;

    private UpdateManualScenario(BaseStepListener baseStepListener,
                                 EnvironmentVariables environmentVariables,
                                 String scenarioDescription) {
        this.baseStepListener = baseStepListener;
        this.manualScenarioChecker = new ManualScenarioChecker(environmentVariables);
        this.scenarioDescription = scenarioDescription;
    }

    public static UpdateManualScenarioBuilder forScenario(String description) {
        return new UpdateManualScenarioBuilder(description);
    }

    public void updateManualScenario(TestResult result, List<Tag> scenarioTags) {

        Optional<String> lastTestedVersion = manualScenarioChecker.lastTestedVersionFromTags(scenarioTags);
        Optional<String> testEvidence = manualScenarioChecker.testEvidenceFromTags(scenarioTags);
        Boolean manualTestIsUpToDate = manualScenarioChecker.scenarioResultIsUpToDate(scenarioTags);

        if (!manualTestIsUpToDate) {
            updateCurrentScenarioResultTo(TestResult.PENDING, lastTestedVersion, manualTestIsUpToDate, testEvidence);
        } else if (isUnsuccessful(result)) {
            recordManualFailureForCurrentScenarioWithResult(result, lastTestedVersion, manualTestIsUpToDate, testEvidence);
        } else {
            updateCurrentScenarioResultTo(result, lastTestedVersion, manualTestIsUpToDate, testEvidence);
        }
    }

    private void recordManualFailureForCurrentScenarioWithResult(TestResult result,
                                                                 Optional<String> lastTestedVersion,
                                                                 Boolean manualTestIsUpToDate,
                                                                 Optional<String> testEvidence) {
        String failureMessage = failureMessageFrom(scenarioDescription)
                                .orElse(result.getAdjective() + " manual test");

        baseStepListener.latestTestOutcome().ifPresent(
                outcome -> outcome.setTestFailureMessage(failureMessage)
        );
        updateCurrentScenarioResultTo(result, lastTestedVersion, manualTestIsUpToDate, testEvidence);

    }

    private void updateCurrentScenarioResultTo(TestResult result,
                                               Optional<String> lastTestedVersion,
                                               Boolean manualTestIsUpToDate,
                                               Optional<String> testEvidence) {
        baseStepListener.overrideResultTo(result);
        baseStepListener.recordManualTestResult(result, lastTestedVersion, manualTestIsUpToDate, testEvidence);
    }

    private Optional<String> failureMessageFrom(String description) {
        if (description == null || description.isEmpty()) {
            return Optional.empty();
        }
        String firstLine = description.split("\r?\n")[0];
        if (firstLine.trim().toLowerCase().startsWith("failure:")) {
            return Optional.of("Failed manual test: " + firstLine.trim().substring(8).trim());
        } else {
            return Optional.empty();
        }
    }

    private boolean isUnsuccessful(TestResult result) {
        return (result == TestResult.FAILURE || result == TestResult.ERROR || result == TestResult.COMPROMISED);
    }

    public static class UpdateManualScenarioBuilder {
        private String description;

        UpdateManualScenarioBuilder(String description) {
            this.description = description;
        }

        UpdateManualScenario inContext(BaseStepListener baseStepListener,
                                       EnvironmentVariables environmentVariables) {
            return new UpdateManualScenario(baseStepListener, environmentVariables, description);
        }
    }
}
