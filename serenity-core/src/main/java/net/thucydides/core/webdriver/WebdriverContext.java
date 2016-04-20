package net.thucydides.core.webdriver;

import org.openqa.selenium.WebDriver;

public class WebdriverContext {

    private final WebdriverManager webdriverManager;
    private final String context;

    public WebdriverContext(WebdriverManager webdriverManager, String context) {
        this.webdriverManager = webdriverManager;
        this.context = context;
    }

    public WebDriver getWebdriver() {
        String driverType = webdriverManager.getCurrentDriverType();
        String driverName = driverType + ":" + context;
        return webdriverManager.getWebdriver(driverName);
    }
}
