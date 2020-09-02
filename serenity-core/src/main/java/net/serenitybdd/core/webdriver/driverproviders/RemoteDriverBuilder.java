package net.serenitybdd.core.webdriver.driverproviders;

import com.google.common.base.Splitter;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import net.serenitybdd.core.buildinfo.DriverCapabilityRecord;
import net.serenitybdd.core.di.WebDriverInjectors;
import net.serenitybdd.core.exceptions.SerenityManagedException;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.appium.AppiumConfiguration;
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

            switch (AppiumConfiguration.from(environmentVariables).definedTargetPlatform()) {

                case IOS:
                    Capabilities iosCapabilities = AppiumConfiguration.from(environmentVariables).getCapabilities(options);
                    driver = new IOSDriver<>(remoteUrl, iosCapabilities.merge(remoteCapabilities));
                    break;

                case ANDROID:
                    Capabilities androidCapabilities = AppiumConfiguration.from(environmentVariables).getCapabilities(options);
                    driver = new AndroidDriver<>(remoteUrl, androidCapabilities.merge(remoteCapabilities));
                    break;

                case NONE:
                default:
                    driver = new RemoteWebDriver(remoteUrl, remoteCapabilities);
                    break;
            }

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
