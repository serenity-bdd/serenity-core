package net.thucydides.core.steps.events;


public class SetTestPendingEvent
    extends StepEventBusEventBase {

	@Override
	public void play() {
		getStepEventBus().testPending();
	}
}
