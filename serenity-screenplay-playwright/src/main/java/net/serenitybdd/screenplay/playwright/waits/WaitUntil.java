package net.serenitybdd.screenplay.playwright.waits;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

import java.time.Duration;
import java.util.regex.Pattern;

/**
 * Wait for elements or page states using Playwright's native waiting mechanisms.
 *
 * <p>Playwright has built-in auto-waiting for most operations, but sometimes you need
 * explicit waits for dynamic content. This class provides a fluent API for waiting.</p>
 *
 * <p>Sample usage:</p>
 * <pre>
 *     actor.attemptsTo(
 *         WaitUntil.the("#element").isVisible(),
 *         WaitUntil.the("#loading").isHidden(),
 *         WaitUntil.the("#button").isEnabled(),
 *         WaitUntil.the("#result").containsText("Success"),
 *         WaitUntil.the("#element").isVisible().forNoMoreThan(Duration.ofSeconds(30)),
 *         WaitUntil.thePageIsLoaded(),
 *         WaitUntil.networkIsIdle(),
 *         WaitUntil.theUrl().contains("/dashboard")
 *     );
 * </pre>
 */
public class WaitUntil {

    private final Target target;

    private WaitUntil(Target target) {
        this.target = target;
    }

    /**
     * Wait for an element identified by a selector.
     */
    public static WaitUntil the(String selector) {
        return new WaitUntil(Target.the(selector).locatedBy(selector));
    }

    /**
     * Wait for a Target element.
     */
    public static WaitUntil the(Target target) {
        return new WaitUntil(target);
    }

    /**
     * Wait for the page to finish loading (DOMContentLoaded).
     */
    public static Performable thePageIsLoaded() {
        return new WaitForLoadState(LoadState.DOMCONTENTLOADED);
    }

    /**
     * Wait for network to be idle (no pending requests for 500ms).
     */
    public static Performable networkIsIdle() {
        return new WaitForLoadState(LoadState.NETWORKIDLE);
    }

    /**
     * Wait for the page to fully load (including all resources).
     */
    public static Performable allResourcesToLoad() {
        return new WaitForLoadState(LoadState.LOAD);
    }

    /**
     * Wait for URL conditions.
     */
    public static UrlWaitBuilder theUrl() {
        return new UrlWaitBuilder();
    }

    /**
     * Wait for element to be visible.
     */
    public WaitForElementState isVisible() {
        return new WaitForElementState(target, WaitForSelectorState.VISIBLE);
    }

    /**
     * Wait for element to be hidden (not visible or not attached).
     */
    public WaitForElementState isHidden() {
        return new WaitForElementState(target, WaitForSelectorState.HIDDEN);
    }

    /**
     * Wait for element to be attached to DOM.
     */
    public WaitForElementState isAttached() {
        return new WaitForElementState(target, WaitForSelectorState.ATTACHED);
    }

    /**
     * Wait for element to be detached from DOM.
     */
    public WaitForElementState isDetached() {
        return new WaitForElementState(target, WaitForSelectorState.DETACHED);
    }

    /**
     * Wait for element to be enabled.
     * Uses Playwright assertions which have built-in retry logic.
     */
    public WaitForEnabled isEnabled() {
        return new WaitForEnabled(target, null);
    }

    /**
     * Wait for element to be disabled.
     * Uses Playwright assertions which have built-in retry logic.
     */
    public WaitForDisabled isDisabled() {
        return new WaitForDisabled(target, null);
    }

    /**
     * Wait for element to contain specific text.
     * Uses Playwright assertions which have built-in retry logic.
     */
    public WaitForText containsText(String text) {
        return new WaitForText(target, text, null);
    }

    /**
     * Wait for element to have specific text (exact match).
     */
    public WaitForExactText hasText(String text) {
        return new WaitForExactText(target, text, null);
    }
}

/**
 * Wait for element to reach a specific state.
 */
class WaitForElementState implements Performable {
    private final Target target;
    private final WaitForSelectorState state;
    private Double timeoutMs;

    WaitForElementState(Target target, WaitForSelectorState state) {
        this.target = target;
        this.state = state;
    }

    /**
     * Set a custom timeout for the wait.
     */
    public WaitForElementState forNoMoreThan(Duration timeout) {
        this.timeoutMs = (double) timeout.toMillis();
        return this;
    }

    @Override
    @Step("{0} waits for #target to be " + "#state")
    public <T extends Actor> void performAs(T actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        Locator locator = target.resolveFor(page);
        Locator.WaitForOptions options = new Locator.WaitForOptions().setState(state);
        if (timeoutMs != null) {
            options.setTimeout(timeoutMs);
        }
        locator.waitFor(options);
    }

    @Override
    public String toString() {
        return "wait for " + target + " to be " + state.name().toLowerCase();
    }
}

/**
 * Wait for page load state.
 */
class WaitForLoadState implements Performable {
    private final LoadState state;

    WaitForLoadState(LoadState state) {
        this.state = state;
    }

    @Override
    @Step("{0} waits for page load state: " + "#state")
    public <T extends Actor> void performAs(T actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        page.waitForLoadState(state);
    }

    @Override
    public String toString() {
        return "wait for " + state.name().toLowerCase();
    }
}

/**
 * Builder for URL wait conditions.
 */
class UrlWaitBuilder {

    /**
     * Wait for URL to contain a substring.
     */
    public Performable contains(String substring) {
        return new WaitForUrlContains(substring, null);
    }

    /**
     * Wait for URL to match exactly.
     */
    public Performable toBe(String url) {
        return new WaitForUrlExact(url, null);
    }

    /**
     * Wait for URL to match a pattern.
     */
    public Performable toMatch(Pattern pattern) {
        return new WaitForUrlPattern(pattern, null);
    }
}

/**
 * Wait for URL to contain substring.
 */
class WaitForUrlContains implements Performable {
    private final String substring;
    private final Double timeoutMs;

    WaitForUrlContains(String substring, Double timeoutMs) {
        this.substring = substring;
        this.timeoutMs = timeoutMs;
    }

    public WaitForUrlContains forNoMoreThan(Duration timeout) {
        return new WaitForUrlContains(substring, (double) timeout.toMillis());
    }

    @Override
    @Step("{0} waits for URL to contain '" + "#substring" + "'")
    public <T extends Actor> void performAs(T actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        Page.WaitForURLOptions options = new Page.WaitForURLOptions();
        if (timeoutMs != null) {
            options.setTimeout(timeoutMs);
        }
        page.waitForURL(url -> url.contains(substring), options);
    }
}

/**
 * Wait for URL to match exactly.
 */
class WaitForUrlExact implements Performable {
    private final String url;
    private final Double timeoutMs;

    WaitForUrlExact(String url, Double timeoutMs) {
        this.url = url;
        this.timeoutMs = timeoutMs;
    }

    @Override
    @Step("{0} waits for URL to be '" + "#url" + "'")
    public <T extends Actor> void performAs(T actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        Page.WaitForURLOptions options = new Page.WaitForURLOptions();
        if (timeoutMs != null) {
            options.setTimeout(timeoutMs);
        }
        page.waitForURL(url, options);
    }
}

/**
 * Wait for URL to match pattern.
 */
class WaitForUrlPattern implements Performable {
    private final Pattern pattern;
    private final Double timeoutMs;

    WaitForUrlPattern(Pattern pattern, Double timeoutMs) {
        this.pattern = pattern;
        this.timeoutMs = timeoutMs;
    }

    @Override
    @Step("{0} waits for URL to match pattern")
    public <T extends Actor> void performAs(T actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        Page.WaitForURLOptions options = new Page.WaitForURLOptions();
        if (timeoutMs != null) {
            options.setTimeout(timeoutMs);
        }
        page.waitForURL(pattern, options);
    }
}

/**
 * Wait for element to be enabled using Playwright assertions with built-in retry.
 */
class WaitForEnabled implements Performable {
    private final Target target;
    private final Double timeoutMs;

    WaitForEnabled(Target target, Double timeoutMs) {
        this.target = target;
        this.timeoutMs = timeoutMs;
    }

    public WaitForEnabled forNoMoreThan(Duration timeout) {
        return new WaitForEnabled(target, (double) timeout.toMillis());
    }

    @Override
    @Step("{0} waits for #target to be enabled")
    public <T extends Actor> void performAs(T actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        Locator locator = target.resolveFor(page);
        var assertion = PlaywrightAssertions.assertThat(locator);
        if (timeoutMs != null) {
            assertion.isEnabled(new com.microsoft.playwright.assertions.LocatorAssertions.IsEnabledOptions()
                .setTimeout(timeoutMs));
        } else {
            assertion.isEnabled();
        }
    }
}

/**
 * Wait for element to be disabled using Playwright assertions with built-in retry.
 */
class WaitForDisabled implements Performable {
    private final Target target;
    private final Double timeoutMs;

    WaitForDisabled(Target target, Double timeoutMs) {
        this.target = target;
        this.timeoutMs = timeoutMs;
    }

    public WaitForDisabled forNoMoreThan(Duration timeout) {
        return new WaitForDisabled(target, (double) timeout.toMillis());
    }

    @Override
    @Step("{0} waits for #target to be disabled")
    public <T extends Actor> void performAs(T actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        Locator locator = target.resolveFor(page);
        var assertion = PlaywrightAssertions.assertThat(locator);
        if (timeoutMs != null) {
            assertion.isDisabled(new com.microsoft.playwright.assertions.LocatorAssertions.IsDisabledOptions()
                .setTimeout(timeoutMs));
        } else {
            assertion.isDisabled();
        }
    }
}

/**
 * Wait for element to contain text using Playwright assertions with built-in retry.
 */
class WaitForText implements Performable {
    private final Target target;
    private final String text;
    private final Double timeoutMs;

    WaitForText(Target target, String text, Double timeoutMs) {
        this.target = target;
        this.text = text;
        this.timeoutMs = timeoutMs;
    }

    public WaitForText forNoMoreThan(Duration timeout) {
        return new WaitForText(target, text, (double) timeout.toMillis());
    }

    @Override
    @Step("{0} waits for #target to contain text '" + "#text" + "'")
    public <T extends Actor> void performAs(T actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        Locator locator = target.resolveFor(page);
        var assertion = PlaywrightAssertions.assertThat(locator);
        if (timeoutMs != null) {
            assertion.containsText(text, new com.microsoft.playwright.assertions.LocatorAssertions.ContainsTextOptions()
                .setTimeout(timeoutMs));
        } else {
            assertion.containsText(text);
        }
    }
}

/**
 * Wait for element to have exact text using Playwright assertions with built-in retry.
 */
class WaitForExactText implements Performable {
    private final Target target;
    private final String text;
    private final Double timeoutMs;

    WaitForExactText(Target target, String text, Double timeoutMs) {
        this.target = target;
        this.text = text;
        this.timeoutMs = timeoutMs;
    }

    public WaitForExactText forNoMoreThan(Duration timeout) {
        return new WaitForExactText(target, text, (double) timeout.toMillis());
    }

    @Override
    @Step("{0} waits for #target to have text '" + "#text" + "'")
    public <T extends Actor> void performAs(T actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        Locator locator = target.resolveFor(page);
        var assertion = PlaywrightAssertions.assertThat(locator);
        if (timeoutMs != null) {
            assertion.hasText(text, new com.microsoft.playwright.assertions.LocatorAssertions.HasTextOptions()
                .setTimeout(timeoutMs));
        } else {
            assertion.hasText(text);
        }
    }
}
