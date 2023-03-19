package net.thucydides.core.steps.events;


import net.serenitybdd.core.webdriver.RemoteDriver;
import net.thucydides.core.steps.session.TestSession;
import net.thucydides.core.webdriver.SerenityWebdriverManager;
import net.thucydides.core.webdriver.ThucydidesWebDriverSupport;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.SessionId;

public class ExampleFinishedEvent extends StepEventBusEventBase {

	private SessionId webSessionId;

	private String driverUsedInThisTest;

	private WebDriver webDriver;

	public ExampleFinishedEvent() {
		saveCurrentWebDriverContext();
	}

	private void saveCurrentWebDriverContext() {
		WebDriver currentDriver = SerenityWebdriverManager.inThisTestThread().getCurrentDriver();
		if (currentDriver != null) {
			SessionId sessionId = RemoteDriver.of(currentDriver).getSessionId();
			setWebSessionId(sessionId);
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
		getStepEventBus().exampleFinished();
	}

	public String toString() {
		return("EventBusEvent EXAMPLE_FINISHED_EVENT ");
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
