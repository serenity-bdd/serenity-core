package net.thucydides.core.steps.events;


import net.thucydides.core.steps.session.TestSession;

import java.util.Map;

public class ExampleStartedEvent extends StepEventBusEventBase {

	private Map<String, String> data;
	private String exampleName;


	public ExampleStartedEvent(Map<String, String> data, String exampleName) {
		this.data = data;
		this.exampleName = exampleName;
		TestSession.getTestSessionContext().setCurrentTestName(exampleName);
	}


	@Override
	public void play() {
		TestSession.getTestSessionContext().setCurrentTestName(exampleName);
		getStepEventBus().exampleStarted(data,exampleName,timestamp);
	}

	public String toString() {
		return("EventBusEvent EXAMPLE_STARTED_EVENT " + data + " " + exampleName);
	}
}
