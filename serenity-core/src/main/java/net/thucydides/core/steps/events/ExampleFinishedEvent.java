package net.thucydides.core.steps.events;


public class ExampleFinishedEvent extends StepEventBusEventBase {

	public ExampleFinishedEvent() {}

	@Override
	public void play() {
		getStepEventBus().exampleFinished();
	}

	public String toString() {
		return("EventBusEvent EXAMPLE_FINISHED_EVENT ");
	}
}
