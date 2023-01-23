package net.thucydides.core.steps.events;

import java.time.ZonedDateTime;

public class TestStartedEvent extends StepEventBusEventBase {


	private String testName;

	private String id;

	private ZonedDateTime startTime;

	public TestStartedEvent(String scenarioId, final String testName, final String id) {
		super(scenarioId);
		this.testName =  testName;
		this.id = id;
		this.startTime = ZonedDateTime.now();
	}


	@Override
	public void play() {
		getStepEventBus().testStarted(testName,id,startTime);
	}

	public String toString() {
		return("EventBusEvent TEST_STARTED_EVENT " + testName + " " + id);
	}
}
