package net.thucydides.core.pages.integration;

import net.serenitybdd.core.pages.PageObject;
import net.thucydides.core.annotations.DefaultUrl;
import net.thucydides.model.util.EnvironmentVariables;
import org.openqa.selenium.WebDriver;

@DefaultUrl("classpath:static-site/fancy-tabs-demo.html")
public class ShadowDomSitePage extends PageObject {

    public ShadowDomSitePage(WebDriver driver) {
        super(driver);
    }

    public ShadowDomSitePage(WebDriver driver, EnvironmentVariables environmentVariables) {
        super(driver, environmentVariables);
    }

    public ShadowDomSitePage(WebDriver driver, int timeout) {
        super(driver, timeout);
    }
}
