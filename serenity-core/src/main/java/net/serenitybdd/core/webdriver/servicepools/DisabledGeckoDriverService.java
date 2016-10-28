package net.serenitybdd.core.webdriver.servicepools;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class DisabledGeckoDriverService extends org.openqa.selenium.firefox.GeckoDriverService {

    public DisabledGeckoDriverService() throws IOException {
        super(new File(""), 0, null, null);
    }

    public static DisabledGeckoDriverService build() {
        try {
            return new DisabledGeckoDriverService();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public URL getUrl() {
        return null;
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public void start() throws IOException {}

    @Override
    public void stop() {}
}
