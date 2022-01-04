package net.thucydides.core.webdriver.shadow;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Locate shadow dom elements using Selenium 4.
 */
public class ByShadow extends By {

    private final String target;
    private final String shadowHost;
    private final String[] innerShadowHosts;

    protected ByShadow(String target, String shadowHost, String[] innerShadowHosts) {
        this.target = target;
        this.shadowHost = shadowHost;
        this.innerShadowHosts = innerShadowHosts;
    }

    public static ByShadow cssSelector(String target, String shadowHost, String... innerShadowHosts) {
        return new ByShadow(target, shadowHost, innerShadowHosts);
    }

    @Override
    public WebElement findElement(SearchContext context) {
        return shadowHostFor(context).findElement(By.cssSelector(target));
    }

    @Override
    public List<WebElement> findElements(SearchContext context) {
        return shadowHostFor(context).findElements(By.cssSelector(target));
    }

    private SearchContext shadowHostFor(SearchContext context) {
        WebElement shadowHostElement = context.findElement(By.cssSelector(shadowHost));
        for(String innerHost : innerShadowHosts) {
            shadowHostElement = shadowHostElement.getShadowRoot().findElement(By.cssSelector(innerHost));
        }
        return shadowHostElement.getShadowRoot();
    }
}
