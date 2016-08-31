package net.serenitybdd.core.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ResolvedWebElementResolver extends WebElementResolver {
    private final WebElement webElement;

    public ResolvedWebElementResolver(WebElement webElement) {
        this.webElement = webElement;
    }

    @Override
    public WebElement resolveForDriver(WebDriver driver) {
        return webElement;
    }
}
