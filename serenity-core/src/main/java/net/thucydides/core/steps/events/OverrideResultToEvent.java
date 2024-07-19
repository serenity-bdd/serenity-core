package net.thucydides.core.steps.events;

import net.thucydides.model.domain.TestResult;

public class OverrideResultToEvent
    extends StepEventBusEventBase {


	private final TestResult testResult;

	public OverrideResultToEvent( final TestResult testResult) {
		this.testResult = testResult;
	}


	@Override
	public void play() {
		getStepEventBus().getBaseStepListener().overrideResultTo(testResult);
	}

	public String toString() {
		return("EventBusEvent OVERRIDE_RESULT_TO " + testResult);
	}
}
