package net.thucydides.core.steps.events;


import java.util.Map;

public class ExampleStartedEvent extends StepEventBusEventBase {

	private Map<String, String> data;
	private String exampleName;


	public ExampleStartedEvent(Map<String, String> data, String exampleName) {
		this.data = data;
		this.exampleName = exampleName;
	}


	@Override
	public void play() {
		getStepEventBus().exampleStarted(data,exampleName);
	}

	public String toString() {
		return("EventBusEvent EXAMPLE_STARTED_EVENT " + data + " " + exampleName);
	}
}
