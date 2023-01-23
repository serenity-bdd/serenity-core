package net.thucydides.core.steps.events;



public class SetTestIgnoredEvent
		extends StepEventBusEventBase {

	@Override
	public void play() {
		getStepEventBus().testIgnored();
	}
}
