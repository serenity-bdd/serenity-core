package net.thucydides.core.steps.events;


public class UpdateOverallResultsEvent extends StepEventBusEventBase {

	@Override
	public void play() {
		getStepEventBus().updateOverallResults();
	}

	public String toString() {
		return("EventBusEvent UPDATE_OVERALL_EVENTS " );
	}
}
