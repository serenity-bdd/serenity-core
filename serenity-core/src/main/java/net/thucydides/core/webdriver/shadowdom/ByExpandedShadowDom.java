package net.thucydides.core.webdriver.shadowdom;

import net.serenitybdd.core.annotations.findby.By;
import org.apache.commons.io.IOUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * By Selector that finds Shadow Dom elements.
 *
 * Based on implementation from https://github.com/Georgegriff/query-selector-shadow-dom
 * QuerySelector that can pierce Shadow DOM roots without knowing the path through nested shadow roots.
 */
@Deprecated
public class ByExpandedShadowDom extends By {

    private final String shadowDomSelector;

    private ByExpandedShadowDom(final String selector) {
        this.shadowDomSelector = selector;
    }

    public static ByExpandedShadowDom of(String selector) {
        return new ByExpandedShadowDom(selector);
    }

    @Override
    public List<WebElement> findElements(SearchContext context) {
        List<WebElement> rootElements = context.findElements(By.cssSelector(shadowDomSelector));
        return rootElements.stream().map(element -> expandRootElement(context,element)).collect(Collectors.toList());
    }

    @Override
    public WebElement findElement(SearchContext context) {
        WebElement rootElement = context.findElement(By.cssSelector(shadowDomSelector));
        return expandRootElement(context,rootElement);
    }

    @Override
    public String toString() {
        return "By.shadowDomSelector: " + shadowDomSelector;
    }


    private WebElement expandRootElement(SearchContext context, WebElement element) {
        return (WebElement) ((JavascriptExecutor) context)
                .executeScript("return arguments[0].shadowRoot",element);
    }
}
