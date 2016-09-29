package net.serenitybdd.core.support;

import net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.edge.EdgeDriverService;

import java.io.File;

import static net.thucydides.core.ThucydidesSystemProperty.WEBDRIVER_IE_DRIVER;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class EdgeService extends ManagedDriverService {
    public EdgeService(EnvironmentVariables environmentVariables) {
        super(
                isNotEmpty(environmentVariables.getProperty(WEBDRIVER_IE_DRIVER)) ?
                        new EdgeDriverService.Builder().usingAnyFreePort()
                                .usingDriverExecutable(new File(environmentVariables.getProperty(WEBDRIVER_IE_DRIVER)))
                                .build()
                        : new EdgeDriverService.Builder()
                        .usingAnyFreePort()
                        .build()
        );
    }
}
