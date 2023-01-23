package net.thucydides.core.steps.events;


public class StepIgnoredEvent extends StepEventBusEventBase {

	@Override
	public void play() {
		getStepEventBus().stepIgnored();
	}

	public String toString() {
		return("EventBusEvent STEP_IGNORED_EVENT ");
	}
}
