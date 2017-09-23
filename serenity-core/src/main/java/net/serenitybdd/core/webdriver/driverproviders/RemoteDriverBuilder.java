package net.serenitybdd.core.webdriver.driverproviders;

import com.google.common.base.Splitter;
import net.serenitybdd.core.buildinfo.DriverCapabilityRecord;
import net.serenitybdd.core.exceptions.SerenityManagedException;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.webdriver.stubs.WebDriverStub;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;

import java.io.IOException;
import java.net.*;
import java.util.List;

abstract class RemoteDriverBuilder {

    private final DriverCapabilityRecord driverProperties;

    private final static int WITHIN_THREE_SECONDS = 3000;

    RemoteDriverBuilder() {
        this.driverProperties = Injectors.getInjector().getInstance(DriverCapabilityRecord.class);
    }

    abstract WebDriver buildWithOptions(String options) throws MalformedURLException;


    WebDriver newRemoteDriver(URL remoteUrl, Capabilities capabilities) {
        if (StepEventBus.getEventBus().webdriverCallsAreSuspended()) {
            return new WebDriverStub();
        }

        try {
            ensureHostIsAvailableAt(remoteUrl);

            RemoteWebDriver driver = new RemoteWebDriver(remoteUrl, capabilities);
            driverProperties.registerCapabilities(capabilities.getBrowserName(), driver.getCapabilities());
            return driver;
        } catch (UnreachableBrowserException unreachableBrowser) {
            String errorMessage = unreachableBrowserErrorMessage(unreachableBrowser);
            throw new SerenityManagedException(errorMessage, unreachableBrowser);
        } catch (UnknownHostException unknownHost) {
            throw new SerenityManagedException(unknownHost.getMessage(), unknownHost);
        }
    }

    private void ensureHostIsAvailableAt(URL remoteUrl) throws UnknownHostException {
        if (!hostIsAvailableAt(remoteUrl)) {
            theRemoteServerIsUnavailable(remoteUrl.getHost() + " could not be reached");
        }
    }

    private boolean hostIsAvailableAt(URL remoteUrl) {
        try {
            URLConnection urlConnection = remoteUrl.openConnection();
            urlConnection.connect();
            return true;
        } catch (IOException e) {
            return false; // Either timeout or unreachable or failed DNS lookup.
        }
    }


    public static boolean pingHost(String host, int port, int timeout) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeout);
            return true;
        } catch (IOException e) {
            return false; // Either timeout or unreachable or failed DNS lookup.
        }
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
