package net.thucydides.core.steps.events;

public class StepPendingEvent
    extends StepEventBusEventBase {

	private String message;

	public StepPendingEvent() {}


	public StepPendingEvent(String message) {
		this.message = message;
	}

	@Override
	public void play() {
		if (message == null) {
			getStepEventBus().stepPending();
		} else {
			getStepEventBus().stepPending(message);
		}
	}

	public String toString() {
		return("EventBusEvent STEP_PENDING_EVENT ");
	}
}
