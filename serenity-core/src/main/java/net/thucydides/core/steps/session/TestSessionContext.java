package net.thucydides.core.steps.session;

import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.steps.events.StepEventBusEvent;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.SessionId;

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

	private String driverUsedInThisTest;

	private WebDriver webDriver;


	public List<StepEventBusEvent> getStepEventBusEvents() {
		return stepEventBusEvents;
	}

	public void addStepBusEvent(StepEventBusEvent event) {
		event.setStepEventBus(stepEventBus);
		stepEventBusEvents.add(event);
	}

	private SessionId webSessionId;

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

	public SessionId getWebSessionId() {
		return webSessionId;
	}

	public void setWebSessionId(SessionId webSessionId) {
		this.webSessionId = webSessionId;
	}

	public String getDriverUsedInThisTest() {
		return driverUsedInThisTest;
	}

	public void setDriverUsedInThisTest(String driverUsedInThisTest) {
		this.driverUsedInThisTest = driverUsedInThisTest;
	}

	public WebDriver getWebDriver() {
		return webDriver;
	}

	public void setWebDriver(WebDriver webDriver) {
		this.webDriver = webDriver;
	}
}
