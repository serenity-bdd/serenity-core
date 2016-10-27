package net.serenitybdd.core.webdriver.servicepools;

import io.github.bonigarcia.wdm.PhantomJsDriverManager;
import net.thucydides.core.ThucydidesSystemProperty;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;

import java.io.File;

import static net.thucydides.core.ThucydidesSystemProperty.PHANTOMJS_BINARY_PATH;

public class PhantomJSServicePool extends DriverServicePool<PhantomJSDriverService> {
    @Override
    protected PhantomJSDriverService newDriverService() {
        PhantomJSDriverService newService = new PhantomJSDriverService.Builder()
                .usingAnyFreePort()
                .usingPhantomJSExecutable(phantomJSBinary())
                .build();

        DriverPathConfiguration.updateSystemProperty(PHANTOMJS_BINARY_PATH.getPropertyName())
                               .withExecutablePath(phantomJSBinary());

        Runtime.getRuntime().addShutdownHook(new StopServiceHook(newService));

        return newService;
    }

    private File phantomJSBinary() {
        if (ThucydidesSystemProperty.AUTOMATIC_DRIVER_DOWNLOAD.booleanFrom(environmentVariables, true)) {
            PhantomJsDriverManager.getInstance().setup();
        }

        return DriverServiceExecutable.called("phantomjs")
                .usingEnvironmentVariables(environmentVariables)
                .withSystemProperty(PHANTOMJS_BINARY_PATH.getPropertyName())
                .andDownloadableFrom("http://phantomjs.org/")
                .asAFile();
    }

    protected String serviceName(){ return "phantomjs"; }

    @Override
    protected WebDriver newDriverInstance(Capabilities capabilities) {
        return new PhantomJSDriver(capabilities);
    }
}
