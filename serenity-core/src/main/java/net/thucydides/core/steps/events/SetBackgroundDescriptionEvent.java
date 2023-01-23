package net.thucydides.core.steps.events;


public class SetBackgroundDescriptionEvent extends StepEventBusEventBase {

	private String backgroundDescription;

	public SetBackgroundDescriptionEvent(String backgroundDescription) {
		this.backgroundDescription =  backgroundDescription;
	}

	@Override
	public void play() {
		getStepEventBus().setBackgroundDescription(backgroundDescription);
	}
}
