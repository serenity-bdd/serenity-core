package net.thucydides.core.steps.session;

import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.steps.events.StepEventBusEvent;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class TestSessionContext {

	private String sessionId;

	private StepEventBus stepEventBus;

	private String currentTestName;

	private AtomicBoolean sessionStarted = new AtomicBoolean(false);

	private List<StepEventBusEvent> stepEventBusEvents = Collections.synchronizedList(new LinkedList<>());

	public AtomicBoolean getSessionStarted() {
		return sessionStarted;
	}

	public List<StepEventBusEvent> getStepEventBusEvents() {
		return stepEventBusEvents;
	}

	public void addStepBusEvent(StepEventBusEvent event) {
		event.setStepEventBus(stepEventBus);
		stepEventBusEvents.add(event);
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

	public String getCurrentTestName() {
		return currentTestName;
	}

	public void setCurrentTestName(String currentTestName) {
		this.currentTestName = currentTestName;
	}
}
