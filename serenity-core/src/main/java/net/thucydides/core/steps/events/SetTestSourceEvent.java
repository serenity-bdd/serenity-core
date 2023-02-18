package net.thucydides.core.steps.events;

import net.thucydides.core.steps.StepEventBus;

public class SetTestSourceEvent extends StepEventBusEventBase {

	public SetTestSourceEvent(String testSource) {
		this.testSource =  testSource;
	}

	private String testSource;


	@Override
	public void play() {
		getStepEventBus().setTestSource(testSource);

		//TODO - to be fixed - newTestOutcome.setTestSource(StepEventBus.getParallelEventBus().getTestSource());
		// BaseStepListener->recordNewTestOutcome: newTestOutcome.setTestSource(StepEventBus.getParallelEventBus().getTestSource());
		getStepEventBus().setTestSource(testSource);
		StepEventBus.getParallelEventBus().setTestSource(testSource);

	}
}
