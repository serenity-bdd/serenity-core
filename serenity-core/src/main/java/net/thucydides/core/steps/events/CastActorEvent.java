package net.thucydides.core.steps.events;

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
