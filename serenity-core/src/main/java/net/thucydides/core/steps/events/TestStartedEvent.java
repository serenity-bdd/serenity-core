package net.thucydides.core.steps.events;

import java.time.ZonedDateTime;

public class TestStartedEvent extends StepEventBusEventBase {


	private String testName;

	private String id;

	private ZonedDateTime startTime;

	public TestStartedEvent(final String testName) {
		this.testName =  testName;
		this.startTime = ZonedDateTime.now();
	}



	public TestStartedEvent(String scenarioId, final String testName, final String id) {
		super(scenarioId);
		this.testName =  testName;
		this.id = id;
		this.startTime = ZonedDateTime.now();
	}


	@Override
	public void play() {
		if(getScenarioId() != null) {
			getStepEventBus().testStarted(testName, id, startTime);
		}
		else {
			getStepEventBus().testStarted(testName);
		}
	}

	public String toString() {
		return("EventBusEvent TEST_STARTED_EVENT " + testName + " " + id);
	}
}
