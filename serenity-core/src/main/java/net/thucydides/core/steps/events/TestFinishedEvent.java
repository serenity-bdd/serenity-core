package net.thucydides.core.steps.events;

import net.serenitybdd.core.webdriver.RemoteDriver;
import net.thucydides.core.steps.session.TestSession;
import net.thucydides.core.webdriver.SerenityWebdriverManager;
import net.thucydides.core.webdriver.ThucydidesWebDriverSupport;
import net.thucydides.core.webdriver.WebDriverFacade;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.SessionId;


public class TestFinishedEvent extends StepEventBusEventBase {

	private boolean inDataTest;

	private SessionId webSessionId;

	private String driverUsedInThisTest;

	private WebDriver webDriver;

	public TestFinishedEvent() {
		saveCurrentWebDriverContext();
	}

	public TestFinishedEvent(String scenarioId, boolean inDataDrivenTest) {
		super(scenarioId);
		this.inDataTest = inDataDrivenTest;
		saveCurrentWebDriverContext();
	}


	private void saveCurrentWebDriverContext() {
		WebDriverFacade currentDriver = (WebDriverFacade) SerenityWebdriverManager.inThisTestThread().getCurrentDriver();
		if (currentDriver != null && currentDriver.isInstantiated()) {
			if (RemoteDriver.isARemoteDriver(currentDriver)) {
				SessionId sessionId = RemoteDriver.of(currentDriver).getSessionId();
				setWebSessionId(sessionId);
			}
			setWebDriver(currentDriver);
			setDriverUsedInThisTest(ThucydidesWebDriverSupport.getDriversUsed());
		}
	}


	@Override
	public void play() {
		if (getWebSessionId() != null) {
			TestSession.getTestSessionContext().setWebSessionId(getWebSessionId());
		}
		if (getWebDriver() != null) {
			TestSession.getTestSessionContext().setWebDriver(getWebDriver());
		}
		if (getDriverUsedInThisTest() != null) {
			TestSession.getTestSessionContext().setDriverUsedInThisTest(getDriverUsedInThisTest());
		}
		if (getScenarioId() != null) {
			getStepEventBus().testFinished(inDataTest, getTimestamp());
		} else {
			getStepEventBus().testFinished(false, getTimestamp());
		}
	}

	public String toString() {
		return("EventBusEvent TEST_FINISHED_EVENT "  + " " + inDataTest + " scenario " + getScenarioId());
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
