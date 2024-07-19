package com.serenity.screenplay.platform.android;

import org.openqa.selenium.remote.DesiredCapabilities;

import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import net.serenitybdd.core.webdriver.enhancers.BeforeAWebdriverScenario;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.util.EnvironmentVariables;
import net.thucydides.core.webdriver.SupportedWebDriver;

/**
 * Simple way to extend the Android WebDriver capabilities and customize the driver creation activities.
 * <p>
 * 
 * Usage: Add this properties to android.properties
 * <pre>
 * serenity.extension.packages=com.serenity.screenplay.platform.android
 * </pre> 
 * @see <a href="https:serenity-bdd.github.io/theserenitybook/latest/extending-webdriver.html">Extending Webdriver</a>
 * 
 * @author jacob
 */
public class AndroidCapabilityEnhancer implements BeforeAWebdriverScenario {
	
	private final static String ANDROID_DRIVER = "UiAutomator2";
	private final static String APP_PACKAGE_NAME = "com.example.android.testing.notes";
	private final static String APP_ACTIVITY_NAME = "com.example.android.testing.notes.notes.NotesActivity";

	@Override
	public DesiredCapabilities apply(EnvironmentVariables environmentVariables, 
									 SupportedWebDriver driver,
									 TestOutcome testOutcome, 
									 DesiredCapabilities capabilities) {

		//capabilities.setCapability(MobileCapabilityType.NO_RESET, false);
		capabilities.setCapability(MobileCapabilityType.FULL_RESET, true);
		capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 10000);
		capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, ANDROID_DRIVER);
		//capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "ANDROID");
		capabilities.setCapability(AndroidMobileCapabilityType.APP_PACKAGE, APP_PACKAGE_NAME);
		capabilities.setCapability(AndroidMobileCapabilityType.APP_ACTIVITY, APP_ACTIVITY_NAME);
		capabilities.setCapability(AndroidMobileCapabilityType.NATIVE_WEB_SCREENSHOT, true);
		capabilities.setCapability(AndroidMobileCapabilityType.AUTO_GRANT_PERMISSIONS, true); //Noreset=false

		return capabilities;
	}

}
