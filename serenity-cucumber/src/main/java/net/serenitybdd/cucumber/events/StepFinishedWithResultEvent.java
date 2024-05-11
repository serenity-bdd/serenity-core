package net.serenitybdd.cucumber.events;

import io.cucumber.core.plugin.SerenityReporterParallel;
import io.cucumber.plugin.event.*;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.domain.stacktrace.RootCauseAnalyzer;
import net.thucydides.model.screenshots.ScreenshotAndHtmlSource;
import net.thucydides.model.steps.ExecutedStepDescription;
import net.thucydides.model.steps.StepFailure;
import net.thucydides.core.steps.events.StepEventBusEventBase;

import java.time.ZonedDateTime;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class StepFinishedWithResultEvent extends StepEventBusEventBase {

    private static final String OPEN_PARAM_CHAR = "\uff5f";
    private static final String CLOSE_PARAM_CHAR = "\uff60";


	private final Result result;
	private final io.cucumber.messages.types.Step currentStep;
	private final TestStep currentTestStep;
    private final List<ScreenshotAndHtmlSource> screenshotList;
    boolean isInDataDrivenTest;

	public StepFinishedWithResultEvent(Result result,
                                       io.cucumber.messages.types.Step currentStep,
                                       TestStep currentTestStep,
                                       List<ScreenshotAndHtmlSource> screenshotList,
                                       ZonedDateTime time,
                                       boolean isInDataDrivenTest) {
		this.result = result;
		this.currentStep = currentStep;
		this.currentTestStep =  currentTestStep;
		this.screenshotList =  screenshotList;
        this.timestamp = time;
        this.isInDataDrivenTest = isInDataDrivenTest;
	}

	@Override
	public void play() {
 		if (getStepEventBus().currentTestIsSuspended()) {
            getStepEventBus().stepIgnored();
        } else if (Status.PASSED.equals(result.getStatus())) {
            getStepEventBus().stepFinished(screenshotList, getTimestamp());
        } else if (Status.FAILED.equals(result.getStatus())) {
            failed(SerenityReporterParallel.stepTitleFrom(currentStep, currentTestStep), result.getError(), screenshotList);
        } else if (Status.SKIPPED.equals(result.getStatus())) {
            skipped(SerenityReporterParallel.stepTitleFrom(currentStep, currentTestStep), result.getError());
        } else if (Status.PENDING.equals(result.getStatus())) {
            getStepEventBus().stepPending();
        } else if (Status.UNDEFINED.equals(result.getStatus())) {
            getStepEventBus().stepPending();
        }
	}


    private void failed(String stepTitle, Throwable cause, List<ScreenshotAndHtmlSource> screenshots) {
        if (!errorOrFailureRecordedForStep(stepTitle, cause)) {
            if (!isEmpty(stepTitle)) {
                getStepEventBus().updateCurrentStepTitle(stepTitle);
            }
            Throwable rootCause = new RootCauseAnalyzer(cause).getRootCause().toException();
            if (isAssumptionFailure(rootCause)) {
                getStepEventBus().assumptionViolated(rootCause.getMessage());
            } else {
                StepFailure stepFailure = new StepFailure(ExecutedStepDescription.withTitle(normalized(currentStepTitle())), rootCause);
                getStepEventBus().stepFailed(stepFailure, screenshots, isInDataDrivenTest);
            }
        }
    }

    private void skipped(String stepTitle, Throwable cause) {
        if (!errorOrFailureRecordedForStep(stepTitle, cause)) {
            if (!isEmpty(stepTitle)) {
                getStepEventBus().updateCurrentStepTitle(stepTitle);
            }
            if (cause == null) {
                getStepEventBus().stepIgnored();
            } else {
                Throwable rootCause = new RootCauseAnalyzer(cause).getRootCause().toException();
                if (isAssumptionFailure(rootCause)) {
                    getStepEventBus().assumptionViolated(rootCause.getMessage());
                } else {
                    getStepEventBus().stepIgnored();
                }
            }
        }
    }

	private boolean errorOrFailureRecordedForStep(String stepTitle, Throwable cause) {
        if (!latestTestOutcome().isPresent()) {
            return false;
        }
        if (!latestTestOutcome().get().testStepWithDescription(stepTitle).isPresent()) {
            return false;
        }
        Optional<net.thucydides.model.domain.TestStep> matchingTestStep = latestTestOutcome().get().testStepWithDescription(stepTitle);
        if (matchingTestStep.isPresent() && matchingTestStep.get().getNestedException() != null) {
            return (matchingTestStep.get().getNestedException().getOriginalCause() == cause);
        }

        return false;
    }

	private Optional<TestOutcome> latestTestOutcome() {

        if (!getStepEventBus().isBaseStepListenerRegistered()) {
            return Optional.empty();
        }

        List<TestOutcome> recordedOutcomes = getStepEventBus().getBaseStepListener().getTestOutcomes();
        return (recordedOutcomes.isEmpty()) ? Optional.empty()
                : Optional.of(recordedOutcomes.get(recordedOutcomes.size() - 1));
    }

	private boolean isAssumptionFailure(Throwable rootCause) {
        try {
            Class<?> assumptionViolationException = Class.forName("org.junit.AssumptionViolatedException");
            return assumptionViolationException.isAssignableFrom(rootCause.getClass());
        } catch (ClassNotFoundException e) {
            return false;
        }
    }


    private String normalized(String value) {
        return value.replaceAll(OPEN_PARAM_CHAR, "{").replaceAll(CLOSE_PARAM_CHAR, "}");
    }

	private String currentStepTitle() {
        return getStepEventBus().getCurrentStep().isPresent()
                ? getStepEventBus().getCurrentStep().get().getDescription() : "";
    }

}
