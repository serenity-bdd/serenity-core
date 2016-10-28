package net.serenitybdd.core.webdriver.servicepools;

import com.google.common.base.Optional;
import net.thucydides.core.util.EnvironmentVariables;

import java.io.File;

import static net.thucydides.core.ThucydidesSystemProperty.WEBDRIVER_GECKO_DRIVER;

public class GeckoDriverServiceExecutable {


    private final EnvironmentVariables environmentVariables;

    public GeckoDriverServiceExecutable(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public static Optional<File> inEnvironment(EnvironmentVariables environmentVariables) {
        return new GeckoDriverServiceExecutable(environmentVariables).executablePath();
    }

    private Optional<File> executablePath() {
        File geckoBinary = geckoBinaryCalled("geckodriver");

        if (geckoBinary == null || !geckoBinary.exists()) {
            geckoBinary = geckoBinaryCalled("wires");
        }

        return Optional.fromNullable(geckoBinary);
    }

    private File geckoBinaryCalled(String driverName) {
        return DriverServiceExecutable.called(driverName)
                .withSystemProperty(WEBDRIVER_GECKO_DRIVER.getPropertyName())
                .usingEnvironmentVariables(environmentVariables)
                .downloadableFrom("https://github.com/jgraham/wires")
                .asAFile();
    }


}
