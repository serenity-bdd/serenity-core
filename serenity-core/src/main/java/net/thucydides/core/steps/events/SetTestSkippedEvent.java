package net.thucydides.core.steps.events;

public class SetTestSkippedEvent
    extends StepEventBusEventBase {

	@Override
	public void play() {
		getStepEventBus().testSkipped();
	}
}
