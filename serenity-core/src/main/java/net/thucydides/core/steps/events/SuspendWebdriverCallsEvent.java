package net.thucydides.core.steps.events;

public class SuspendWebdriverCallsEvent extends StepEventBusEventBase {

	@Override
	public void play() {
		getStepEventBus().temporarilySuspendWebdriverCalls();
	}

	public String toString() {
		return("EventBusEvent SuspendWebdriverCallsEvent ");
	}
}

