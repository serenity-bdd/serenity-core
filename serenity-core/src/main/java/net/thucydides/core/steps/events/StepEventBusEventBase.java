package net.thucydides.core.steps.events;

import net.thucydides.core.steps.StepEventBus;

import java.time.ZonedDateTime;

public abstract class StepEventBusEventBase implements StepEventBusEvent {

	private StepEventBus stepEventBus;

	private String scenarioId;

	protected ZonedDateTime timestamp;

	public StepEventBusEventBase() {
		this("");
	}

	public StepEventBusEventBase(String scenarioId) {
		this.scenarioId = scenarioId;
		this.timestamp = ZonedDateTime.now();
	}

	public StepEventBus getStepEventBus() {
		return stepEventBus;
	}

	public void setStepEventBus(StepEventBus stepEventBus) {
		this.stepEventBus = stepEventBus;
	}

	public String getScenarioId (){
		return scenarioId;
	}

	public ZonedDateTime getTimestamp() {
		return timestamp;
	}

}
