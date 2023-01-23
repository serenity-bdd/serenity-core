package net.thucydides.core.steps.events;

import net.thucydides.core.steps.StepEventBus;

public class CastActorEvent extends StepEventBusEventBase {

	private String actorName;

	public CastActorEvent( String actorName) {
		this.actorName =  actorName;
	}

	@Override
	public void play() {
		getStepEventBus().castActor(actorName);
	}
}
