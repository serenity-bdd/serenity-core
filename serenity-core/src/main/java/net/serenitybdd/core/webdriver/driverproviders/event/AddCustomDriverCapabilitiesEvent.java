package net.serenitybdd.core.webdriver.driverproviders.event;

import net.serenitybdd.core.webdriver.driverproviders.AddCustomDriverCapabilities;
import net.thucydides.core.steps.TestContext;
import net.thucydides.core.steps.events.StepEventBusEventBase;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.SupportedWebDriver;
import org.openqa.selenium.MutableCapabilities;


public class AddCustomDriverCapabilitiesEvent extends StepEventBusEventBase {


	private EnvironmentVariables environmentVariables;
	private String driverName;
	private MutableCapabilities capabilities;

	public AddCustomDriverCapabilitiesEvent(EnvironmentVariables environmentVariables, String driverName, MutableCapabilities capabilities) {
		this.environmentVariables = environmentVariables;
		this.driverName = driverName;
		this.capabilities = capabilities;
	}


	@Override
	public void play() {
		AddCustomDriverCapabilities.from(environmentVariables)
                        .withTestDetails(SupportedWebDriver.getDriverTypeFor(driverName), getStepEventBus().getBaseStepListener().getCurrentTestOutcome())
                        .to(capabilities);
        //
		// Record browser and platform
		//
        TestContext.forTheCurrentTest().recordBrowserAndPlatformConfiguration(capabilities);
	}
}

