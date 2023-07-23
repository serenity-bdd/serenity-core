package net.thucydides.core.steps.events;

import net.thucydides.core.screenshots.ScreenshotAndHtmlSource;
import net.thucydides.core.steps.StepFailure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class StepFailedEvent
    extends StepEventBusEventBase {

	private static final Logger LOGGER = LoggerFactory.getLogger(StepFailedEvent.class);

	private final StepFailure stepFailure;

	private final List<ScreenshotAndHtmlSource> screenshotList;


	public StepFailedEvent(StepFailure stepFailure, List<ScreenshotAndHtmlSource> screenshotList) {
		this.stepFailure = stepFailure;
		this.screenshotList = screenshotList;
	}


	@Override
	public void play() {
		LOGGER.debug("SRP:PlayStepFinishedEvent with screenshot size "
	 					+ ((screenshotList != null) ?  screenshotList.size() : 0));
		getStepEventBus().stepFailed(stepFailure, screenshotList);
	}

	public String toString() {
		return(String.format("EventBusEvent STEP_FAILED_EVENT with %s and %d",
			stepFailure,((screenshotList != null) ? screenshotList.size() : 0) ));
	}
}
