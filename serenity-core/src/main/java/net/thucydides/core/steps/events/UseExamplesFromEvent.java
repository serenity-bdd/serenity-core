package net.thucydides.core.steps.events;

import net.thucydides.model.domain.DataTable;

public class UseExamplesFromEvent extends StepEventBusEventBase {

	private final DataTable dataTable;

	public UseExamplesFromEvent(DataTable dataTable) {
		this.dataTable =  dataTable;
	}


	@Override
	public void play() {
		getStepEventBus().useExamplesFrom(dataTable);
	}
}
