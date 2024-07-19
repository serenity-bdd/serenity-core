package net.thucydides.core.steps.events;

import net.thucydides.model.domain.DataTable;

public class AddNewExamplesFromEvent extends StepEventBusEventBase {

	private final DataTable dataTable;

	public AddNewExamplesFromEvent(DataTable dataTable) {
		this.dataTable = dataTable;
	}

	@Override
	public void play() {
		getStepEventBus().addNewExamplesFrom(dataTable);
	}
}
