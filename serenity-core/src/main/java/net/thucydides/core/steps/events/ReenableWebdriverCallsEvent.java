package net.thucydides.core.steps.events;

public class ReenableWebdriverCallsEvent extends StepEventBusEventBase {

	@Override
	public void play() {
		getStepEventBus().reenableWebdriverCalls();
	}

	public String toString() {
		return("EventBusEvent ReenableWebdriverCallsEvent ");
	}
}
