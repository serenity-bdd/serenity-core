package net.serenitybdd.rest.event;

import net.serenitybdd.model.rest.RestQuery;
import net.thucydides.core.steps.events.StepEventBusEventBase;

public class RecordRestQueryEvent extends StepEventBusEventBase {

	RestQuery restQuery;

	public RecordRestQueryEvent(RestQuery restQuery) {
		this.restQuery = restQuery;
	}

	@Override
	public void play() {
		getStepEventBus().getBaseStepListener().recordRestQuery(restQuery);
	}

	public String toString() {
		return("EventBusEvent RECORD_REST_QUERY_EVENT " + restQuery);
	}
}
