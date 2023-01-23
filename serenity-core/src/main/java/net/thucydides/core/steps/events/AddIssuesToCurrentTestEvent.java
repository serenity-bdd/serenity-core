package net.thucydides.core.steps.events;


import java.util.List;

public class AddIssuesToCurrentTestEvent extends StepEventBusEventBase {

	private List<String> issues;

	public AddIssuesToCurrentTestEvent(List<String> issues) {
		this.issues = issues;
	}

	@Override
	public void play() {
		getStepEventBus().addIssuesToCurrentStory(issues);
	}
}
