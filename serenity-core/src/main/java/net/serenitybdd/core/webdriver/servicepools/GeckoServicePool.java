package net.serenitybdd.core.webdriver.servicepools;

import com.google.common.base.Optional;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.os.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static com.google.common.base.Preconditions.checkState;
import static net.thucydides.core.ThucydidesSystemProperty.WEBDRIVER_GECKO_DRIVER;

public class GeckoServicePool extends DriverServicePool<GeckoDriverService> {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    protected String serviceName(){ return "gecko"; }

    public GeckoServicePool() {
        super();
        configureGeckoDriverBinaries();
    }

    @Override
    protected GeckoDriverService newDriverService() {
        return new GeckoDriverService.Builder()
                .usingAnyFreePort()
                .build();
    }


    private void configureGeckoDriverBinaries() {
        File geckoBinary = geckoBinaryConfiguredIn(environmentVariables).or(geckoBinaryOnSystemPath());

        if (geckoBinary.exists()) {
            System.setProperty("webdriver.gecko.driver", geckoBinary.getAbsolutePath());
        }
    }

    private Optional<File> geckoBinaryConfiguredIn(EnvironmentVariables environmentVariables) {
        String configuredGeckoDriver = environmentVariables.getProperty(WEBDRIVER_GECKO_DRIVER);
        if (StringUtils.isEmpty(configuredGeckoDriver)) {
            return Optional.absent();
        }
        File configuredGeckoDriverBinary = new File(configuredGeckoDriver);
        if (!configuredGeckoDriverBinary.exists()) {
            LOGGER.warn("No gecko binary found at {}, looking on the system path instead", configuredGeckoDriver);
            return Optional.absent();
        }

        return Optional.of(new File(configuredGeckoDriver));
    }

    private File geckoBinaryOnSystemPath() {
        File geckoBinary;
        try {
            geckoBinary = findExecutable("geckodriver", "webdriver.gecko.driver", "https://github.com/jgraham/wires", "https://github.com/jgraham/wires");
        } catch (IllegalStateException geckodriverNotFoundSoTryWiresBinary) {
            geckoBinary = findExecutable("wires", "webdriver.gecko.driver", "https://github.com/jgraham/wires", "https://github.com/jgraham/wires");
        }
        return geckoBinary;
    }

    protected static File findExecutable(String exeName,
                                         String exeProperty,
                                         String exeDocs,
                                         String exeDownload) {
        String defaultPath = CommandLine.find(exeName);
        String exePath = System.getProperty(exeProperty, defaultPath);
        checkState(exePath != null,
                "The path to the driver executable must be set by the %s system property;"
                        + " for more information, see %s. "
                        + "The latest version can be downloaded from %s",
                exeProperty, exeDocs, exeDownload);

        File exe = new File(exePath);
        checkExecutable(exe);
        return exe;
    }

    protected static void checkExecutable(File exe) {
        checkState(exe.exists(),
                "The driver executable does not exist: %s", exe.getAbsolutePath());
        checkState(!exe.isDirectory(),
                "The driver executable is a directory: %s", exe.getAbsolutePath());
        checkState(FileHandler.canExecute(exe),
                "The driver is not executable: %s", exe.getAbsolutePath());
    }
}
