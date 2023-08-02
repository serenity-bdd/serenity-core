package net.thucydides.core.steps.events;

import net.thucydides.core.steps.ExecutedStepDescription;

import java.time.ZonedDateTime;

public class StepStartedEvent extends StepEventBusEventBase {

	ExecutedStepDescription stepDescription;

	public StepStartedEvent(ExecutedStepDescription stepDescription, ZonedDateTime startTime) {
		this.timestamp = startTime;
		this.stepDescription = stepDescription;
	}

	public StepStartedEvent(ExecutedStepDescription stepDescription) {
		this(stepDescription, ZonedDateTime.now());
	}

	@Override
	public void play() {
		getStepEventBus().stepStarted(stepDescription, timestamp);
	}

	public String toString() {
		return("EventBusEvent STEP_STARTED_EVENT " + stepDescription.getName() + " " + stepDescription.getTitle());
	}
}
