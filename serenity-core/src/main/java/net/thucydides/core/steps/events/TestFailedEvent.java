package net.thucydides.core.steps.events;


public class TestFailedEvent extends StepEventBusEventBase {

	private final Throwable cause;

	public TestFailedEvent(String scenarioId,Throwable cause) {
		super(scenarioId);
		this.cause =  cause;
	}


	@Override
	public void play() {
		getStepEventBus().testFailed(cause, timestamp);
	}
}
