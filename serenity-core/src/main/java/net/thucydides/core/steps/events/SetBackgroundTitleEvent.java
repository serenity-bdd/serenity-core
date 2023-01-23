package net.thucydides.core.steps.events;


public class SetBackgroundTitleEvent extends StepEventBusEventBase {

	private String backgroundTitle;

	public SetBackgroundTitleEvent(String backgroundTitle) {
		this.backgroundTitle =  backgroundTitle;
	}

	@Override
	public void play() {
		getStepEventBus().setBackgroundTitle(backgroundTitle);
	}
}
