package net.thucydides.core.steps.events;


public class AddDescriptionToCurrentTestEvent extends StepEventBusEventBase {

	private String description;

	public AddDescriptionToCurrentTestEvent( String description) {
		this.description =  description;
	}

	@Override
	public void play() {
		getStepEventBus().addDescriptionToCurrentTest(description);
	}
}
