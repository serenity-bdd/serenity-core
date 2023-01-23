package net.thucydides.core.steps.events;


public class UpdateCurrentStepTitleEvent extends StepEventBusEventBase {

	private final String stepTitle;

	public UpdateCurrentStepTitleEvent(String stepTitle) {
		this.stepTitle = stepTitle;
	}


	@Override
	public void play() {
		getStepEventBus().updateCurrentStepTitle(stepTitle);
	}
}
