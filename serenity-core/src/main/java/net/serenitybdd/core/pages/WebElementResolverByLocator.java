package net.serenitybdd.core.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class WebElementResolverByLocator extends WebElementResolver {
    private final By bySelector;

    public WebElementResolverByLocator(By bySelector) {
        this.bySelector = bySelector;
    }

    @Override
    public WebElement resolveForDriver(WebDriver driver) {
        return driver.findElement(bySelector);
    }
}
