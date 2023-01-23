package net.thucydides.core.steps.events;

import net.thucydides.core.model.DataTable;

public class UseExamplesFromEvent extends StepEventBusEventBase {

	private DataTable dataTable;

	public UseExamplesFromEvent(DataTable dataTable) {
		this.dataTable =  dataTable;
	}


	@Override
	public void play() {
		getStepEventBus().useExamplesFrom(dataTable);
	}
}
