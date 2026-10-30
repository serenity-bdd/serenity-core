package net.serenitybdd.screenplay.playwright.assertions;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.assertions.PageAssertions;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

import java.util.regex.Pattern;

/**
 * Perform assertions about the state of elements, pages, and URLs using Playwright's
 * native assertion API with built-in auto-retry.
 *
 * <p>Sample usage:</p>
 * <pre>
 *     actor.attemptsTo(
 *         // Element assertions
 *         Ensure.that("#element").isVisible(),
 *         Ensure.that("#element").hasText("Hello"),
 *         Ensure.that("#element").containsText("ello"),
 *         Ensure.that("#element").hasTextMatching("Hel.*"),
 *         Ensure.that("#element").hasAttribute("class", "active"),
 *         Ensure.that("#element").hasClass("active"),
 *         Ensure.that(".items").hasCount(5),
 *
 *         // URL assertions
 *         Ensure.thatTheCurrentUrl().contains("/dashboard"),
 *
 *         // Page title assertions
 *         Ensure.thatThePageTitle().isEqualTo("Dashboard"),
 *         Ensure.thatThePageTitle().contains("Dashboard")
 *     );
 * </pre>
 */
public class Ensure {

    private final Target target;
    private final Double timeout;

    public Ensure(Target target, Double timeout) {
        this.target = target;
        this.timeout = timeout;
    }

    /**
     * Start an assertion about an element identified by a selector.
     */
    public static Ensure that(String selector) {
        return new Ensure(Target.the(selector).locatedBy(selector), null);
    }

    /**
     * Start an assertion about a Target element.
     */
    public static Ensure that(Target target) {
        return new Ensure(target, null);
    }

    /**
     * Start an assertion about the current URL.
     */
    public static UrlEnsure thatTheCurrentUrl() {
        return new UrlEnsure(null);
    }

    /**
     * Start an assertion about the page title.
     */
    public static PageTitleEnsure thatThePageTitle() {
        return new PageTitleEnsure(null);
    }

    /**
     * Set a custom timeout for this assertion.
     */
    public Ensure withTimeout(Double timeout) {
        return new Ensure(target, timeout);
    }

    private boolean timeoutIsSpecified() {
        return (timeout != null);
    }

    private LocatorAssertions.IsVisibleOptions visibleOptions() {
        return timeoutIsSpecified() ? new LocatorAssertions.IsVisibleOptions().setTimeout(timeout) : null;
    }

    private LocatorAssertions.IsHiddenOptions hiddenOptions() {
        return timeoutIsSpecified() ? new LocatorAssertions.IsHiddenOptions().setTimeout(timeout) : null;
    }

    private LocatorAssertions.IsCheckedOptions checkedOptions() {
        return timeoutIsSpecified() ? new LocatorAssertions.IsCheckedOptions().setTimeout(timeout) : null;
    }

    private LocatorAssertions.IsEnabledOptions enabledOptions() {
        return timeoutIsSpecified() ? new LocatorAssertions.IsEnabledOptions().setTimeout(timeout) : null;
    }

    private LocatorAssertions.IsDisabledOptions disabledOptions() {
        return timeoutIsSpecified() ? new LocatorAssertions.IsDisabledOptions().setTimeout(timeout) : null;
    }

    private LocatorAssertions.IsEditableOptions editableOptions() {
        return timeoutIsSpecified() ? new LocatorAssertions.IsEditableOptions().setTimeout(timeout) : null;
    }

    private LocatorAssertions.IsFocusedOptions focusedOptions() {
        return timeoutIsSpecified() ? new LocatorAssertions.IsFocusedOptions().setTimeout(timeout) : null;
    }

    private LocatorAssertions.IsEmptyOptions emptyOptions() {
        return timeoutIsSpecified() ? new LocatorAssertions.IsEmptyOptions().setTimeout(timeout) : null;
    }

    private LocatorAssertions.HasTextOptions textOptions() {
        return timeoutIsSpecified() ? new LocatorAssertions.HasTextOptions().setTimeout(timeout) : null;
    }

    private LocatorAssertions.ContainsTextOptions containsTextOptions() {
        return timeoutIsSpecified() ? new LocatorAssertions.ContainsTextOptions().setTimeout(timeout) : null;
    }

    private LocatorAssertions.HasValueOptions valueOptions() {
        return timeoutIsSpecified() ? new LocatorAssertions.HasValueOptions().setTimeout(timeout) : null;
    }

    private LocatorAssertions.HasAttributeOptions attributeOptions() {
        return timeoutIsSpecified() ? new LocatorAssertions.HasAttributeOptions().setTimeout(timeout) : null;
    }

    private LocatorAssertions.HasClassOptions classOptions() {
        return timeoutIsSpecified() ? new LocatorAssertions.HasClassOptions().setTimeout(timeout) : null;
    }

    private LocatorAssertions.HasCountOptions countOptions() {
        return timeoutIsSpecified() ? new LocatorAssertions.HasCountOptions().setTimeout(timeout) : null;
    }

    private LocatorAssertions.HasCSSOptions cssOptions() {
        return timeoutIsSpecified() ? new LocatorAssertions.HasCSSOptions().setTimeout(timeout) : null;
    }

    // ==================== Visibility Assertions ====================

    /**
     * Check whether the element is visible.
     */
    public Performable isVisible() {
        return Task.where(target + " should be visible",
            actor -> {
                Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
                Locator locator = target.resolveFor(page);
                PlaywrightAssertions.assertThat(locator).isVisible(visibleOptions());
            }
        );
    }

    /**
     * Check whether the element is not visible.
     */
    public Performable isHidden() {
        return Task.where(target + " should be hidden",
            actor -> {
                Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
                Locator locator = target.resolveFor(page);
                PlaywrightAssertions.assertThat(locator).isHidden(hiddenOptions());
            }
        );
    }

    // ==================== State Assertions ====================

    /**
     * Check whether the element is checked (for checkbox/radio).
     */
    public Performable isChecked() {
        return Task.where(target + " should be checked",
            actor -> {
                Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
                Locator locator = target.resolveFor(page);
                PlaywrightAssertions.assertThat(locator).isChecked(checkedOptions());
            }
        );
    }

    /**
     * Check whether the element is NOT checked.
     */
    public Performable isNotChecked() {
        return Task.where(target + " should not be checked",
            actor -> {
                Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
                Locator locator = target.resolveFor(page);
                LocatorAssertions.IsCheckedOptions options = timeoutIsSpecified()
                    ? new LocatorAssertions.IsCheckedOptions().setTimeout(timeout).setChecked(false)
                    : new LocatorAssertions.IsCheckedOptions().setChecked(false);
                PlaywrightAssertions.assertThat(locator).isChecked(options);
            }
        );
    }

    /**
     * Check whether the element is enabled.
     */
    public Performable isEnabled() {
        return Task.where(target + " should be enabled",
            actor -> {
                Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
                Locator locator = target.resolveFor(page);
                PlaywrightAssertions.assertThat(locator).isEnabled(enabledOptions());
            }
        );
    }

    /**
     * Check whether the element is disabled.
     */
    public Performable isDisabled() {
        return Task.where(target + " should be disabled",
            actor -> {
                Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
                Locator locator = target.resolveFor(page);
                PlaywrightAssertions.assertThat(locator).isDisabled(disabledOptions());
            }
        );
    }

    /**
     * Check whether the element is editable.
     */
    public Performable isEditable() {
        return Task.where(target + " should be editable",
            actor -> {
                Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
                Locator locator = target.resolveFor(page);
                PlaywrightAssertions.assertThat(locator).isEditable(editableOptions());
            }
        );
    }

    /**
     * Check whether the element has focus.
     */
    public Performable isFocused() {
        return Task.where(target + " should be focused",
            actor -> {
                Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
                Locator locator = target.resolveFor(page);
                PlaywrightAssertions.assertThat(locator).isFocused(focusedOptions());
            }
        );
    }

    /**
     * Check whether the input element is empty.
     */
    public Performable isEmpty() {
        return Task.where(target + " should be empty",
            actor -> {
                Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
                Locator locator = target.resolveFor(page);
                PlaywrightAssertions.assertThat(locator).isEmpty(emptyOptions());
            }
        );
    }

    /**
     * Check whether the element is not empty.
     */
    public Performable isNotEmpty() {
        return Task.where(target + " should not be empty",
            actor -> {
                Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
                Locator locator = target.resolveFor(page);
                LocatorAssertions.IsEmptyOptions options = timeoutIsSpecified()
                    ? new LocatorAssertions.IsEmptyOptions().setTimeout(timeout)
                    : null;
                PlaywrightAssertions.assertThat(locator).not().isEmpty(options);
            }
        );
    }

    // ==================== Text Assertions ====================

    /**
     * Check whether the element has the exact text.
     */
    public Performable hasText(String expectedText) {
        return Task.where(target + " should have text '" + expectedText + "'",
            actor -> {
                Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
                Locator locator = target.resolveFor(page);
                PlaywrightAssertions.assertThat(locator).hasText(expectedText, textOptions());
            }
        );
    }

    /**
     * Check whether the element contains the specified text.
     */
    public Performable containsText(String expectedText) {
        return Task.where(target + " should contain text '" + expectedText + "'",
            actor -> {
                Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
                Locator locator = target.resolveFor(page);
                PlaywrightAssertions.assertThat(locator).containsText(expectedText, containsTextOptions());
            }
        );
    }

    /**
     * Check whether the element text matches a regex pattern (as string).
     */
    public Performable hasTextMatching(String regex) {
        return hasTextMatching(Pattern.compile(regex));
    }

    /**
     * Check whether the element text matches a regex pattern.
     */
    public Performable hasTextMatching(Pattern pattern) {
        return Task.where(target + " should have text matching '" + pattern.pattern() + "'",
            actor -> {
                Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
                Locator locator = target.resolveFor(page);
                PlaywrightAssertions.assertThat(locator).hasText(pattern, textOptions());
            }
        );
    }

    // ==================== Value Assertions ====================

    /**
     * Check whether the input element has the expected value.
     */
    public Performable currentValue(String expectedValue) {
        return Task.where(target + " should have value '" + expectedValue + "'",
            actor -> {
                Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
                Locator locator = target.resolveFor(page);
                PlaywrightAssertions.assertThat(locator).hasValue(expectedValue, valueOptions());
            }
        );
    }

    /**
     * Alias for currentValue.
     */
    public Performable hasValue(String expectedValue) {
        return currentValue(expectedValue);
    }

    // ==================== Attribute Assertions ====================

    /**
     * Check whether the element has an attribute with the expected value.
     */
    public Performable hasAttribute(String name, String value) {
        return Task.where(target + " should have attribute '" + name + "' = '" + value + "'",
            actor -> {
                Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
                Locator locator = target.resolveFor(page);
                PlaywrightAssertions.assertThat(locator).hasAttribute(name, value, attributeOptions());
            }
        );
    }

    /**
     * Check whether the element has an attribute containing the expected substring.
     */
    public Performable hasAttributeContaining(String name, String substring) {
        return Task.where(target + " should have attribute '" + name + "' containing '" + substring + "'",
            actor -> {
                Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
                Locator locator = target.resolveFor(page);
                // Get the attribute value and check if it contains the substring
                String attrValue = locator.getAttribute(name);
                if (attrValue == null || !attrValue.contains(substring)) {
                    throw new AssertionError("Expected attribute '" + name + "' to contain '" + substring +
                        "' but was: " + attrValue);
                }
            }
        );
    }

    /**
     * Check whether the element has the specified CSS class.
     */
    public Performable hasClass(String className) {
        return Task.where(target + " should have class '" + className + "'",
            actor -> {
                Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
                Locator locator = target.resolveFor(page);
                // Use hasClass with the class name directly - Playwright checks class attribute
                PlaywrightAssertions.assertThat(locator).hasClass(Pattern.compile(".*\\b" + className + "\\b.*"), classOptions());
            }
        );
    }

    // ==================== Count Assertions ====================

    /**
     * Check that the locator resolves to the expected number of elements.
     */
    public Performable hasCount(int count) {
        return Task.where(target + " should have count " + count,
            actor -> {
                Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
                Locator locator = target.resolveFor(page);
                PlaywrightAssertions.assertThat(locator).hasCount(count, countOptions());
            }
        );
    }

    // ==================== CSS Assertions ====================

    /**
     * Check whether the element has the expected CSS property value.
     */
    public Performable hasCSSValue(String name, String value) {
        return Task.where(target + " should have CSS '" + name + "' = '" + value + "'",
            actor -> {
                Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
                Locator locator = target.resolveFor(page);
                PlaywrightAssertions.assertThat(locator).hasCSS(name, value, cssOptions());
            }
        );
    }
}
