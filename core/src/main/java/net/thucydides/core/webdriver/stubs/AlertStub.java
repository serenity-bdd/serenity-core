package net.thucydides.core.webdriver.stubs;

import org.openqa.selenium.Alert;
import org.openqa.selenium.security.Credentials;

public class AlertStub implements Alert {
    @Override
    public void dismiss() {
    }

    @Override
    public void accept() {
    }

    @Override
    public String getText() {
        return "";
    }

    @Override
    public void sendKeys(String keysToSend) {
    }

    @Override
    public void authenticateUsing(Credentials credentials) {
    }
}
