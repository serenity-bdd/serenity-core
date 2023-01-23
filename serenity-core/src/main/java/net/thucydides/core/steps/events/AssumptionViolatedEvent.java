package net.thucydides.core.steps.events;


public class AssumptionViolatedEvent
    extends StepEventBusEventBase {

	private final String message;

	public AssumptionViolatedEvent(String message) {
		this.message = message;
	}


	@Override
	public void play() {
		getStepEventBus().assumptionViolated(message);
	}
}
