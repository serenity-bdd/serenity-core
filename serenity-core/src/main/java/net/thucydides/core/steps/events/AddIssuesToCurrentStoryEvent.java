package net.thucydides.core.steps.events;


import java.util.List;

public class AddIssuesToCurrentStoryEvent extends StepEventBusEventBase {

	private List<String> issues;


	public AddIssuesToCurrentStoryEvent( List<String> issues) {
		this.issues = issues;
	}


	@Override
	public void play() {
		getStepEventBus().addIssuesToCurrentStory(issues);
	}
}
