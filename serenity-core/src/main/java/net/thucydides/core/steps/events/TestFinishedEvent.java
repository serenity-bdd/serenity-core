package net.thucydides.core.steps.events;

import java.time.ZonedDateTime;

public class TestFinishedEvent extends StepEventBusEventBase {

	private boolean inDataTest;

	private ZonedDateTime finishingTime;

	public TestFinishedEvent() {
		this.finishingTime = ZonedDateTime.now();
	}

	public TestFinishedEvent(String scenarioId,boolean inDataDrivenTest) {
		super(scenarioId);
		this.inDataTest = inDataDrivenTest;
		this.finishingTime = ZonedDateTime.now();
	}

	@Override
	public void play() {
		if (getScenarioId() != null) {
			getStepEventBus().testFinished(inDataTest, finishingTime);
		} else {
			getStepEventBus().testFinished();
		}
	}

	public String toString() {
		return("EventBusEvent TEST_FINISHED_EVENT "  + " " + inDataTest);
	}
}
