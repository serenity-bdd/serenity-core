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
import java.util.List;

import static ch.lambdaj.Lambda.*;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.hamcrest.Matchers.startsWith;

/**
 * Provides BrowserStack specific capabilities
 *
 * @author Imran Khan
 */

public class BrowserStackRemoteDriverCapabilities implements RemoteDriverCapabilities {

    private final EnvironmentVariables environmentVariables;

    public BrowserStackRemoteDriverCapabilities(EnvironmentVariables environmentVariables){
        this.environmentVariables = environmentVariables;
    }

    @Override
    public String getUrl() {
        return ThucydidesSystemProperty.BROWSERSTACK_URL.from(environmentVariables);
    }

    @Override
    public DesiredCapabilities getCapabilities(DesiredCapabilities capabilities) {
        configureBrowserStackCapabilities(capabilities);
        return capabilities;
    }

    private String getPreparedPropertyKey(String propertyKey) {
        String shortenedPropertyKey = propertyKey.replace("browserstack.","");
        if (shortenedPropertyKey.equals("os.version")) {
            return "os_version";
        } else if (shortenedPropertyKey.equals("browser.version")) {
            return "browser_version";
        }
        return shortenedPropertyKey;
    }

    private void configureBrowserStackCapabilities(DesiredCapabilities capabilities) {
        List<String> browserStackProperties = filter(having(on(String.class), startsWith("browserstack.")),
                                                     environmentVariables.getKeys());
        for(String propertyKey : browserStackProperties) {
            String preparedPropertyKey = getPreparedPropertyKey(propertyKey);
            String propertyValue = environmentVariables.getProperty(propertyKey);
            if (isNotEmpty(propertyValue)) {
                capabilities.setCapability(preparedPropertyKey, propertyValue);
                capabilities.setCapability(propertyKey, propertyValue);
            }
        }

        String remotePlatform = environmentVariables.getProperty("remote.platform");
        if (isNotEmpty(remotePlatform)) {
            capabilities.setPlatform(Platform.valueOf(remotePlatform));
        }

        capabilities.setCapability("name",  bestGuessOfTestName());
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
