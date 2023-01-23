package net.thucydides.core.steps.events;

import net.thucydides.core.steps.ExecutedStepDescription;

public class StepStartedEvent extends StepEventBusEventBase {

	ExecutedStepDescription stepDescription;

	public StepStartedEvent(ExecutedStepDescription stepDescription) {
		this.stepDescription = stepDescription;
	}

	@Override
	public void play() {
		getStepEventBus().stepStarted(stepDescription);
	}

	public String toString() {
		return("EventBusEvent STEP_STARTED_EVENT " + stepDescription.getName() + " " + stepDescription.getTitle());
	}
}
