package net.thucydides.core.steps.events;


public class ClearStepFailuresEvent extends StepEventBusEventBase {


	@Override
	public void play() {
		getStepEventBus().clearStepFailures();
	}
}
