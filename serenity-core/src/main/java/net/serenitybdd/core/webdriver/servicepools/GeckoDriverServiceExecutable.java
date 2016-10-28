package net.serenitybdd.core.webdriver.servicepools;

import net.thucydides.core.util.EnvironmentVariables;

import java.io.File;

import static net.thucydides.core.ThucydidesSystemProperty.WEBDRIVER_GECKO_DRIVER;

public class GeckoDriverServiceExecutable {


    private final EnvironmentVariables environmentVariables;

    public GeckoDriverServiceExecutable(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public static File inEnvironment(EnvironmentVariables environmentVariables) {
        return new GeckoDriverServiceExecutable(environmentVariables).executablePath();
    }

    private File executablePath() {
        File geckoBinary = geckoBinaryCalled("geckodriver");

        if (geckoBinary == null || !geckoBinary.exists()) {
            geckoBinary = geckoBinaryCalled("wires");
        }

        checkForPresenceOfBinary();

        return geckoBinary;
    }

    private void checkForPresenceOfBinary() {
        DriverServiceExecutable.called("geckodriver")
                .withSystemProperty(WEBDRIVER_GECKO_DRIVER.getPropertyName())
                .usingEnvironmentVariables(environmentVariables)
                .reportMissingBinary()
                .downloadableFrom("https://github.com/jgraham/wires")
                .asAFile();
    }

    private File geckoBinaryCalled(String driverName) {
        return DriverServiceExecutable.called(driverName)
                .withSystemProperty(WEBDRIVER_GECKO_DRIVER.getPropertyName())
                .usingEnvironmentVariables(environmentVariables)
                .downloadableFrom("https://github.com/jgraham/wires")
                .asAFile();
    }


}
