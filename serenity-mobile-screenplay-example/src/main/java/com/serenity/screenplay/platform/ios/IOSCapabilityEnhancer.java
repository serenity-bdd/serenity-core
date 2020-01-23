/*
 * Copyright (c) 2019 LINE Corporation. All rights reserved.
 * LINE Corporation PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.serenity.screenplay.platform.ios;

import org.openqa.selenium.remote.DesiredCapabilities;

import io.appium.java_client.remote.MobileCapabilityType;
import net.serenitybdd.core.webdriver.enhancers.BeforeAWebdriverScenario;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.SupportedWebDriver;

/**
 * Simple way to extend the Android WebDriver capabilities and customise the driver creation activities.
 * <p>
 * 
 * Usage: Add this properties to android.properties
 * <pre>
 * serenity.extension.packages=com.serenity.screenplay.platform.ios
 * </pre> 
 * @see <a href="https://serenity-bdd.github.io/theserenitybook/latest/extending-webdriver.html">Extending Webdriver</a>
 * 
 * @author jacob
 */
public class IOSCapabilityEnhancer implements BeforeAWebdriverScenario {
	
	@Override
	public DesiredCapabilities apply(EnvironmentVariables environmentVariables, 
									 SupportedWebDriver driver,
									 TestOutcome testOutcome, 
									 DesiredCapabilities capabilities) {

		capabilities.setCapability(MobileCapabilityType.NO_RESET, false);
		capabilities.setCapability(MobileCapabilityType.FULL_RESET, true);
		capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 1000);
		capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "IOS");
		
		return capabilities;
	}

}
