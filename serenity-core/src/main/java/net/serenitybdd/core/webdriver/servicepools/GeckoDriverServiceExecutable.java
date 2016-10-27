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
        File geckoBinary;
        try {
            geckoBinary = geckoBinaryCalled("geckodriver");
        } catch (IllegalStateException geckodriverNotFoundSoTryWiresBinary) {
            geckoBinary = geckoBinaryCalled("wires");
        }
        return geckoBinary;
    }

    private File geckoBinaryCalled(String driverName) {
        return DriverServiceExecutable.called(driverName)
                .withSystemProperty(WEBDRIVER_GECKO_DRIVER.getPropertyName())
                .usingEnvironmentVariables(environmentVariables)
                .andDownloadableFrom("https://github.com/jgraham/wires")
                .asAFile();
    }


}
