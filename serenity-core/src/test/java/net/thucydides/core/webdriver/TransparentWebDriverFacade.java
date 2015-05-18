package net.thucydides.core.webdriver;

import org.openqa.selenium.WebDriver;

public class TransparentWebDriverFacade {

    private final WebDriverFacade webDriverFacade;

    public TransparentWebDriverFacade(final WebDriverFacade webDriverFacade) {
        this.webDriverFacade = webDriverFacade;
    }


    public WebDriver getProxied() {
        return webDriverFacade.proxiedWebDriver;
    }

    public void get(String url) {
        webDriverFacade.get(url);
    }

    public void quit() {
        webDriverFacade.quit();
    }

    public void reset() {
        webDriverFacade.reset();
    }
}

