package net.thucydides.core.steps.events;

import net.thucydides.model.screenshots.ScreenshotAndHtmlSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class StepFinishedEvent extends StepEventBusEventBase {


	private static final Logger LOGGER = LoggerFactory.getLogger(StepFinishedEvent.class);

	private List<ScreenshotAndHtmlSource> screenshotList;

	public StepFinishedEvent() {}

	public StepFinishedEvent( List<ScreenshotAndHtmlSource> screenshotList) {
		this.screenshotList =  screenshotList;
	}


	@Override
	public void play() {
	 	LOGGER.debug("SRP:PlayStepFinishedEvent with screenshot size "
	 					+ ((screenshotList != null) ?  screenshotList.size() : 0));
		getStepEventBus().stepFinished(screenshotList, this.getTimestamp());
	}

	public String toString() {
		return("EventBusEvent STEP_FINISHED_EVENT ");
	}
}
