package net.thucydides.core.webdriver.capabilities;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.NameConverter;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.lang.reflect.Method;
import java.util.Properties;

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

        Properties saucelabsProperties = environmentVariables.getPropertiesWithPrefix("saucelabs.");

        for(String propertyName : saucelabsProperties.stringPropertyNames()) {
            String unprefixedPropertyName = unprefixed(propertyName);
            capabilities.setCapability(propertyName, typed(saucelabsProperties.getProperty(propertyName)));
            capabilities.setCapability(unprefixedPropertyName, typed(saucelabsProperties.getProperty(propertyName)));
        }

        addBuildNumberTo(capabilities);

        configureTestName(capabilities);

        capabilities.setJavascriptEnabled(true);

        return capabilities;
    }

    private void addBuildNumberTo(DesiredCapabilities capabilities) {
        if (environmentVariables.getProperty("BUILD_NUMBER") != null) {
            capabilities.setCapability("build", environmentVariables.getProperty("BUILD_NUMBER"));
        }
    }


    private void configureBrowserVersion(DesiredCapabilities capabilities) {
        String driverVersion = ThucydidesSystemProperty.SAUCELABS_DRIVER_VERSION.from(environmentVariables);
        if (isNotEmpty(driverVersion)) {
            capabilities.setCapability("version", driverVersion);
        }
    }

    private void configureTargetPlatform(DesiredCapabilities capabilities) {
        SetAppropriateSaucelabsPlatformVersion.inCapabilities(capabilities).from(environmentVariables);

        String remotePlatform = environmentVariables.getProperty("remote.platform");
        if (isNotEmpty(remotePlatform)) {
            capabilities.setPlatform(Platform.valueOf(remotePlatform));
        }

    }

    private Object typed(String value) {
        if (isABoolean(value)) {
            return Boolean.parseBoolean(value);
        }
        if (isAnInteger(value)) {
            return Integer.parseInt(value);
        }
        return value;
    }

    private boolean isAnInteger(String value) {
        return StringUtils.isNumeric(value);
    }

    private boolean isABoolean(String value) {
        return value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false") ;
    }

    private String unprefixed(String propertyName) {
        return propertyName.replace("saucelabs.","");
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
}
