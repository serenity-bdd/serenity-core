package serenitycore.net.serenitybdd.core.webdriver.driverproviders;

import com.google.common.base.Splitter;
import serenitymodel.net.serenitybdd.core.buildinfo.DriverCapabilityRecord;
import serenitycore.net.serenitybdd.core.di.WebDriverInjectors;
import serenitymodel.net.serenitybdd.core.exceptions.SerenityManagedException;
import serenitycore.net.thucydides.core.steps.StepEventBus;
import serenitymodel.net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.List;

abstract class RemoteDriverBuilder {

    private final DriverCapabilityRecord driverProperties;
    protected final EnvironmentVariables environmentVariables;

    RemoteDriverBuilder(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
        this.driverProperties = WebDriverInjectors.getInjector().getInstance(DriverCapabilityRecord.class);
    }

    abstract WebDriver buildWithOptions(String options) throws MalformedURLException;

    WebDriver newRemoteDriver(URL remoteUrl, Capabilities remoteCapabilities, String options) {
        if (StepEventBus.getEventBus().webdriverCallsAreSuspended()) {
            return RemoteWebdriverStub.from(environmentVariables);
        }

        try {
            ensureHostIsAvailableAt(remoteUrl);

            RemoteWebDriver driver;
            driver = new RemoteWebDriver(remoteUrl, remoteCapabilities);

            driverProperties.registerCapabilities(remoteCapabilities.getBrowserName(),  CapabilitiesToPropertiesConverter.capabilitiesToProperties(driver.getCapabilities()));
            return driver;

        } catch (UnreachableBrowserException unreachableBrowser) {
            String errorMessage = unreachableBrowserErrorMessage(unreachableBrowser);
            throw new SerenityManagedException(errorMessage, unreachableBrowser);
        } catch (UnknownHostException unknownHost) {
            throw new SerenityManagedException(unknownHost.getMessage(), unknownHost);
        }
    }

    private void ensureHostIsAvailableAt(URL remoteUrl) throws UnknownHostException {
        try {
            hostShouldBeAvailableAt(remoteUrl);
        } catch (IOException hostUnavailable) {
            theRemoteServerIsUnavailable(remoteUrl.getHost() + " could not be reached: "
                                         + hostUnavailable.getLocalizedMessage());
        }
    }

    private void hostShouldBeAvailableAt(URL remoteUrl) throws IOException {
            URLConnection urlConnection = remoteUrl.openConnection();
            urlConnection.connect();
    }

    private void theRemoteServerIsUnavailable(String message) throws UnknownHostException {
        throw new UnknownHostException(message);
    }

    private String unreachableBrowserErrorMessage(Exception unreachableBrowser) {
        List<String> errorLines = Splitter.onPattern("\n").splitToList(unreachableBrowser.getLocalizedMessage());
        Throwable cause = unreachableBrowser.getCause();
        String errorCause = ((cause == null) ? "" :
                System.lineSeparator() + cause.getClass().getSimpleName() + " - " + cause.getLocalizedMessage());
        return errorLines.get(0) + errorCause;
    }


}
