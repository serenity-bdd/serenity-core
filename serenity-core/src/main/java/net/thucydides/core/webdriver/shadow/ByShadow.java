package net.thucydides.core.webdriver.shadow;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Locate shadow dom elements using Selenium 4.
 * Suppose we have the following HTML structure containing shadow DOM elements.
 * <code>
 *     <pre>
 *         &lt;div id="shadow-host"&gt;
 *           #shadow-root
 *           &lt;input id="shadowedInput"/&gt;
 *
 *           &lt;div id="nested-shadow-host"&gt;
 *               #shadow-root
 *               &lt;input id="nestedShadowedInput"/&gt;
 *           &lt;/div&gt;
 *         &lt;/div&gt;
 *     </pre>
 * </code>
 * <p>
 * To find the first input element inside a single-level shadow DOM, you need to provide the locator for the nested element
 * and the locator for the shadow host element:
 * <code><pre>ByShadow.cssSelector("#shadowedInput","#shadow-host")</pre></code>
 * </p>
 *
 * <p>
 * To find the input element inside the nested shadow DOM, you need to provide the locator for the shadowed element,
 * as well as the list of parent shadow dom locators, rom top to bottom:
 * <code><pre>ByShadow.cssSelector("#shadowedInput","#shadow-host", "#inner-shadow-host")</pre></code>
 * </p>
 */
public class ByShadow extends By implements By.Remotable {

    private final By target;
    private final String shadowHost;
    private final String[] innerShadowHosts;
    private final By.Remotable.Parameters params;


    protected ByShadow(By target, String shadowHost, String[] innerShadowHosts) {
        this.target = target;
        this.shadowHost = shadowHost;
        this.innerShadowHosts = innerShadowHosts;
        this.params = new By.Remotable.Parameters("shadow dom locator", target + " inside shadow dom located by " + shadowHost);
    }

    public static ByShadow cssSelector(String target, String shadowHost, String... innerShadowHosts) {
        return new ByShadow(By.cssSelector(target), shadowHost, innerShadowHosts);
    }

    public static ByShadow cssSelector(By target, String shadowHost, String... innerShadowHosts) {
        return new ByShadow(target, shadowHost, innerShadowHosts);
    }

    @Override
    public String toString() {
        List<String> allRoots = new ArrayList<>();
        allRoots.add(shadowHost);
        for(String innerHost : innerShadowHosts) {
            allRoots.add(innerHost);
        }
        return "By Shadow DOM: find the " + target + " element inside shadow doms: " + allRoots;
    }

    public static ByShadowBuilder css(String target) {
        return new ByShadowBuilder(By.cssSelector(target));
    }

    public static ByShadowBuilder located(By byLocator) {
        return new ByShadowBuilder(byLocator);
    }

    public ByShadow thenInHost(String nestedHost) {
        List<String> innerHosts = new ArrayList<>(Arrays.asList(innerShadowHosts));

        innerHosts.add(nestedHost);
        return new ByShadow(target, shadowHost, innerHosts.toArray(new String[]{}));
    }

    @Override
    public Parameters getRemoteParameters() {
        return this.params;
    }

    public static class ByShadowBuilder {
        private final By target;

        public ByShadowBuilder(By target) {
            this.target = target;
        }

        public ByShadow inHost(String shadowHost) {
            return new ByShadow(target, shadowHost, new String[]{});
        }

        public ByShadow inHosts(String shadowHost, String... innerShadowHosts) {
            return new ByShadow(target, shadowHost, innerShadowHosts);
        }

    }

    @Override
    public WebElement findElement(SearchContext context) {
        return shadowHostFor(context).findElement(target);
    }

    @Override
    public List<WebElement> findElements(SearchContext context) {
        return shadowHostFor(context).findElements(target);
    }

    private SearchContext shadowHostFor(SearchContext context) {
        WebElement shadowHostElement = context.findElement(By.cssSelector(shadowHost));
        for(String innerHost : innerShadowHosts) {
            shadowHostElement = shadowHostElement.getShadowRoot().findElement(By.cssSelector(innerHost));
        }
        return shadowHostElement.getShadowRoot();
    }
}
