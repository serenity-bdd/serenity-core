package net.serenitybdd.core.webdriver;

import net.serenitybdd.core.webdriver.driverproviders.InsecureCertConfig;
import net.thucydides.model.util.EnvironmentVariables;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;
import org.openqa.selenium.firefox.FirefoxOptions;

import static net.thucydides.model.ThucydidesSystemProperty.*;

public class FirefoxOptionsEnhancer {
    private final FirefoxOptions options;

    public FirefoxOptionsEnhancer(FirefoxOptions options) {

        this.options = options;
    }

    public static FirefoxOptionsEnhancer enhanceOptions(FirefoxOptions options) {
        return new FirefoxOptionsEnhancer(options);
    }

    public void using(EnvironmentVariables environmentVariables) {
        options.setHeadless(HEADLESS_MODE.booleanFrom(environmentVariables,false));
        options.setAcceptInsecureCerts(InsecureCertConfig.acceptInsecureCertsDefinedIn(environmentVariables).orElse(false));
        FirefoxDriverLogLevel logLevel = FirefoxDriverLogLevel.fromString(FIREFOX_LOG_LEVEL.from(environmentVariables,"ERROR"));
        options.setLogLevel(logLevel);
    }
}
