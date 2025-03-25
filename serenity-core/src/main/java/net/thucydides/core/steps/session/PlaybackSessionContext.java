package net.thucydides.core.steps.session;

import net.thucydides.core.steps.StepEventBus;
import java.util.concurrent.atomic.AtomicBoolean;

public class PlaybackSessionContext {

	private String sessionId;

	private StepEventBus stepEventBus;

	private final AtomicBoolean sessionStarted = new AtomicBoolean(false);

	public AtomicBoolean getSessionStarted() {
		return sessionStarted;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getSessionId() {
		return sessionId;
	}

	public StepEventBus getStepEventBus() {
		return stepEventBus;
	}

	public void setStepEventBus(StepEventBus stepEventBus) {
		this.stepEventBus = stepEventBus;
	}
}
