package net.thucydides.core.steps.events;

import net.thucydides.core.steps.StepFailure;

public class StepFailedEvent
    extends StepEventBusEventBase {

	private final StepFailure stepFailure;

	public StepFailedEvent(StepFailure stepFailure) {
		this.stepFailure = stepFailure;
	}

	@Override
	public void play() {
		getStepEventBus().stepFailed(stepFailure);
	}

	public String toString() {
		return("EventBusEvent STEP_FAILED_EVENT " + stepFailure);
	}
}
