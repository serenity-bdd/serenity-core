package net.thucydides.core.steps.events;

import net.thucydides.core.steps.StepEventBus;

public abstract class StepEventBusEventBase implements StepEventBusEvent {

	private StepEventBus stepEventBus;

	private String scenarioId;

	public StepEventBusEventBase() {
		this.scenarioId = "";
	}

	public StepEventBusEventBase(String scenarioId) {
		this.scenarioId = scenarioId;
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

}
