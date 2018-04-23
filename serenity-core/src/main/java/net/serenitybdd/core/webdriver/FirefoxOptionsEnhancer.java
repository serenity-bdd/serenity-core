package net.serenitybdd.core.webdriver;

import net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;
import org.openqa.selenium.firefox.FirefoxOptions;

import static net.thucydides.core.ThucydidesSystemProperty.ACCEPT_INSECURE_CERTIFICATES;
import static net.thucydides.core.ThucydidesSystemProperty.FIREFOX_LOG_LEVEL;
import static net.thucydides.core.ThucydidesSystemProperty.HEADLESS_MODE;

public class FirefoxOptionsEnhancer {
    private FirefoxOptions options;

    public FirefoxOptionsEnhancer(FirefoxOptions options) {

        this.options = options;
    }

    public static FirefoxOptionsEnhancer enhanceOptions(FirefoxOptions options) {
        return new FirefoxOptionsEnhancer(options);
    }

    public void using(EnvironmentVariables environmentVariables) {
        options.setHeadless(HEADLESS_MODE.booleanFrom(environmentVariables,false));
        options.setAcceptInsecureCerts(ACCEPT_INSECURE_CERTIFICATES.booleanFrom(environmentVariables,false));
        FirefoxDriverLogLevel logLevel = FirefoxDriverLogLevel.fromString(FIREFOX_LOG_LEVEL.from(environmentVariables,"ERROR"));
        options.setLogLevel(logLevel);
    }
}
