package net.thucydides.core.steps.events;


public class UseScenarioOutlineEvent extends StepEventBusEventBase {

	private String scenarioOutline;

	public UseScenarioOutlineEvent(String scenarioOutline) {
		this.scenarioOutline = scenarioOutline;
	}

	@Override
	public void play() {
		getStepEventBus().useScenarioOutline(scenarioOutline);
	}
}
