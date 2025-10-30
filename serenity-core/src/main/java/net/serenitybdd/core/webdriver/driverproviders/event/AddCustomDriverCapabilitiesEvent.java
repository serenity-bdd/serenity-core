package net.serenitybdd.core.webdriver.driverproviders.event;

import net.serenitybdd.core.webdriver.driverproviders.AddCustomDriverCapabilities;
import net.thucydides.core.steps.TestContext;
import net.thucydides.core.steps.events.StepEventBusEventBase;
import net.thucydides.core.webdriver.SupportedWebDriver;
import net.thucydides.model.util.EnvironmentVariables;
import org.openqa.selenium.MutableCapabilities;


public class AddCustomDriverCapabilitiesEvent extends StepEventBusEventBase {


	private final EnvironmentVariables environmentVariables;
	private final String driverName;
	private final MutableCapabilities capabilities;

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

