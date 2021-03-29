package serenitycore.net.thucydides.core.annotations.locators;

import serenitycore.net.thucydides.core.webdriver.MobilePlatform;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;

import java.lang.reflect.Field;
import java.util.List;
import serenitycore.net.serenitybdd.core.annotations.locators.SmartAnnotations;


public class SmartElementLocator implements ElementLocator {
    private final SearchContext searchContext;
    private final boolean shouldCache;
    private final By by;
    private WebElement cachedElement;
    private List<WebElement> cachedElementList;

    public SmartElementLocator(SearchContext searchContext, Field field, MobilePlatform mobilePlatform) {
        this.searchContext = searchContext;
        SmartAnnotations annotations =
                new SmartAnnotations(field, mobilePlatform);
        shouldCache = annotations.isLookupCached();
        by = annotations.buildBy();
    }

    /**
     * Find the element.
     */
    public WebElement findElement() {
        if (cachedElement != null && shouldCache) {
            return cachedElement;
        }

        WebElement element = searchContext.findElement(by);
        if (shouldCache) {
            cachedElement = element;
        }

        return element;
    }

    /**
     * Find the element list.
     */
    public List<WebElement> findElements() {
        if (cachedElementList != null && shouldCache) {
            return cachedElementList;
        }

        List<WebElement> elements = searchContext.findElements(by);
        if (shouldCache) {
            cachedElementList = elements;
        }

        return elements;
    }

}
