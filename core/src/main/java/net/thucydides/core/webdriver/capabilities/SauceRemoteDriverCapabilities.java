package net.thucydides.core.webdriver.capabilities;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.NameConverter;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.lang.reflect.Method;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Provide Sauce Labs specific capabilities
 *
 * @author Imran Khan
 */
public class SauceRemoteDriverCapabilities implements RemoteDriverCapabilities {

    private final EnvironmentVariables environmentVariables;

    public SauceRemoteDriverCapabilities(EnvironmentVariables environmentVariables){
        this.environmentVariables = environmentVariables;
    }

    @Override
    public String getUrl() {
        return ThucydidesSystemProperty.SAUCELABS_URL.from(environmentVariables);
    }

    @Override
    public DesiredCapabilities getCapabilities(DesiredCapabilities capabilities) {

        configureBrowserVersion(capabilities);

        configureTargetPlatform(capabilities);

        configureTestName(capabilities);

        capabilities.setJavascriptEnabled(true);

        return capabilities;
    }

    private void configureBrowserVersion(DesiredCapabilities capabilities) {
        String driverVersion = ThucydidesSystemProperty.SAUCELABS_DRIVER_VERSION.from(environmentVariables);
        if (isNotEmpty(driverVersion)) {
            capabilities.setCapability("version", driverVersion);
        }
    }

    private void configureTargetPlatform(DesiredCapabilities capabilities) {
        String platformValue = ThucydidesSystemProperty.SAUCELABS_TARGET_PLATFORM.from(environmentVariables);
        if (isNotEmpty(platformValue)) {
            capabilities.setCapability("platform", platformFrom(platformValue));
        }
        if (capabilities.getBrowserName().equals("safari"))
        {
            setAppropriateSaucelabsPlatformVersionForSafari(capabilities);
        }
    }

    private void setAppropriateSaucelabsPlatformVersionForSafari(DesiredCapabilities capabilities)
    {
        if (ThucydidesSystemProperty.SAUCELABS_DRIVER_VERSION.from(environmentVariables).equalsIgnoreCase("mac"))
        {
            String browserVersion = ThucydidesSystemProperty.SAUCELABS_DRIVER_VERSION.from(environmentVariables);
            if (browserVersion.equals("5"))
            {
                capabilities.setCapability("platform", "OS X 10.6");
            }
            else if (browserVersion.equals("6"))
            {
                capabilities.setCapability("platform", "OS X 10.8");
            }
            else if (browserVersion.equals("7"))
            {
                capabilities.setCapability("platform", "OS X 10.9");
            }
        }
    }

    private void configureTestName(DesiredCapabilities capabilities) {
        String testName = ThucydidesSystemProperty.SAUCELABS_TEST_NAME.from(environmentVariables);
        if (isNotEmpty(testName)) {
            capabilities.setCapability("name", testName);
        } else {
            String guessedTestName = bestGuessOfTestName();
            if (guessedTestName != null) {
                capabilities.setCapability("name", bestGuessOfTestName());
            }
        }
    }

    private String bestGuessOfTestName() {
        for (StackTraceElement elt : Thread.currentThread().getStackTrace()) {
            try {
                Class callingClass = Class.forName(elt.getClassName());
                Method callingMethod = callingClass.getMethod(elt.getMethodName());
                if (isATestMethod(callingMethod)) {
                    return NameConverter.humanize(elt.getMethodName());
                } else if (isASetupMethod(callingMethod)) {
                    return NameConverter.humanize(callingClass.getSimpleName());
                }
            } catch (ClassNotFoundException e) {
            } catch (NoSuchMethodException e) {
            }
        }
        return null;
    }

    private boolean isATestMethod(Method callingMethod) {
        return callingMethod.getAnnotation(Test.class) != null;
    }

    private boolean isASetupMethod(Method callingMethod) {
        return (callingMethod.getAnnotation(Before.class) != null)
                || (callingMethod.getAnnotation(BeforeClass.class) != null);
    }

    private Platform platformFrom(String platformValue) {
        return Platform.valueOf(platformValue.toUpperCase());
    }
}
