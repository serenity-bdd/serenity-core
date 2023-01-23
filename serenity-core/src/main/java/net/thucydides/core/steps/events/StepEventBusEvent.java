package net.thucydides.core.steps.events;

import net.thucydides.core.steps.StepEventBus;

public interface StepEventBusEvent {
	String getScenarioId();
	void play();
	void setStepEventBus(StepEventBus stepEventBus);
}
