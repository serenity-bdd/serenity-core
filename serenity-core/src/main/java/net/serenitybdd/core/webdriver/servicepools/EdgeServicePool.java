package net.serenitybdd.core.webdriver.servicepools;

import org.openqa.selenium.edge.EdgeDriverService;

import java.io.File;

import static net.thucydides.core.ThucydidesSystemProperty.WEBDRIVER_IE_DRIVER;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class EdgeServicePool extends DriverServicePool<EdgeDriverService> {

    @Override
    protected EdgeDriverService newDriverService() {

        return isNotEmpty(environmentVariables.getProperty(WEBDRIVER_IE_DRIVER)) ?
                new EdgeDriverService.Builder().usingAnyFreePort()
                        .usingDriverExecutable(new File(environmentVariables.getProperty(WEBDRIVER_IE_DRIVER)))
                        .build()
                : new EdgeDriverService.Builder()
                .usingAnyFreePort()
                .build();
    }

    protected String serviceName(){ return "edge"; }

}
