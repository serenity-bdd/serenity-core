package net.serenitybdd.core.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Collections;
import java.util.List;

public class ResolvedWebElementResolver extends WebElementResolver {
    private final WebElement webElement;

    public ResolvedWebElementResolver(WebElement webElement) {
        this.webElement = webElement;
    }

    @Override
    public WebElement resolveForDriver(WebDriver driver) {
        return webElement;
    }

    @Override
    public List<WebElement> resolveAllForDriver(WebDriver driver) {
        return Collections.singletonList(webElement);
    }
}
