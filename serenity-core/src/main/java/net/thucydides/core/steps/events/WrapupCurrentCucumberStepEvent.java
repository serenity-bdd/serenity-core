package net.thucydides.core.steps.events;


public class WrapupCurrentCucumberStepEvent extends StepEventBusEventBase {

	@Override
	public void play() {
		getStepEventBus().wrapUpCurrentCucumberStep();
	}

	public String toString() {
		return("EventBusEvent WRAPUP_CURRENT_CUCUMBER_STEP_EVENT ");
	}
}
