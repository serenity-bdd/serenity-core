package net.thucydides.core.steps.events;


public class UpdateExampleLineNumberEvent extends StepEventBusEventBase {

	private int exampleLine;

	public UpdateExampleLineNumberEvent(int exampleLine) {
		this.exampleLine = exampleLine;
	}


	@Override
	public void play() {
		getStepEventBus().updateExampleLineNumber(exampleLine);
	}

	public String toString() {
		return("EventBusEvent UPDATE_EXAMPLE_LINE_NUMBER " + exampleLine);
	}
}
