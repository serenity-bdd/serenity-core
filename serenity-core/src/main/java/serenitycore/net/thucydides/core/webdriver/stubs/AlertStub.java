package serenitycore.net.thucydides.core.webdriver.stubs;

import org.openqa.selenium.Alert;

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
}
