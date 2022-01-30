package net.serenitybdd.screenplay.playwright.assertions;


import com.microsoft.playwright.Page;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Perform assertions about the state of elements located using Playwright selectors.
 */
public class Ensure {

    private Target target;
    private Double timeout;

    public Ensure(Target target, Double timeout) {
        this.target = target;
        this.timeout = timeout;
    }

    public static Ensure that(String selector) {
        return new Ensure(Target.the(selector).locatedBy(selector), null);
    }

    public static Ensure that(Target target) {
        return new Ensure(target, null);
    }

    public Ensure withTimeout(Double timeout) {
        return new Ensure(target, timeout);
    }

    private boolean timeoutIsSpecified() {
        return (timeout != null);
    }

    /**
     * Check whether the element is visible. A selector that does not match any elements is considered not visible.
     */
    public Performable isVisible() {
        return Task.where(target + " should be visible",
            actor -> {
                boolean elementIsVisible;
                if (timeoutIsSpecified()) {
                    Page.IsVisibleOptions options = new Page.IsVisibleOptions().setTimeout(timeout);
                    elementIsVisible = BrowseTheWebWithPlaywright.as(actor).getCurrentPage().isVisible(target.asSelector(), options);
                } else {
                    elementIsVisible = BrowseTheWebWithPlaywright.as(actor).getCurrentPage().isVisible(target.asSelector());
                }
                assertThat(elementIsVisible).withFailMessage("Expecting <%s> to be visible", target).isTrue();
            }
        );
    }

    /**
     * Check whether the element is not visible. A selector that does not match any elements is considered not visible.
     */
    public Performable isHidden() {
        return Task.where(target + " should be hidden",
            actor -> {
                boolean elementIsHidden;
                if (timeoutIsSpecified()) {
                    Page.IsHiddenOptions options = new Page.IsHiddenOptions().setTimeout(timeout);
                    elementIsHidden = BrowseTheWebWithPlaywright.as(actor).getCurrentPage().isHidden(target.asSelector(), options);
                } else {
                    elementIsHidden = BrowseTheWebWithPlaywright.as(actor).getCurrentPage().isHidden(target.asSelector());
                }
                assertThat(elementIsHidden).withFailMessage("Expecting <%s> to be hidden", target).isTrue();
            }
        );
    }

    /**
     * Check whether the element is has expected value. Throws for non-input elements.
     */
    public Performable currentValue(String expectedValue) {
        return Task.where(target + " should have value " + expectedValue,
            actor -> {
                String currentValue;
                Page currentPage = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
                if (timeoutIsSpecified()) {
                    Page.InputValueOptions options = new Page.InputValueOptions().setTimeout(timeout);
                    currentValue = currentPage.inputValue(target.asSelector(), options);
                } else {
                    currentValue = currentPage.inputValue(target.asSelector());
                }
                assertThat(currentValue)
                    .describedAs("Expecting <%s> to have different value", target)
                    .isEqualTo(expectedValue);
            }
        );
    }

    /**
     * Check whether the element is checked. Throws if the element is not a checkbox or radio input.
     */
    public Performable isChecked() {
        return Task.where(target + " should be checked",
            actor -> {
                boolean elementIsChecked;
                if (timeoutIsSpecified()) {
                    Page.IsCheckedOptions options = new Page.IsCheckedOptions().setTimeout(timeout);
                    elementIsChecked = BrowseTheWebWithPlaywright.as(actor).getCurrentPage().isChecked(target.asSelector(), options);
                } else {
                    elementIsChecked = BrowseTheWebWithPlaywright.as(actor).getCurrentPage().isChecked(target.asSelector());
                }
                assertThat(elementIsChecked).withFailMessage("Expecting <%s> to be checked", target).isTrue();
            }
        );
    }

    /**
     * Check whether the element is NOT checked. Throws if the element is not a checkbox or radio input.
     */
    public Performable isNotChecked() {
        return Task.where(target + " should not be checked",
            actor -> {
                boolean elementIsChecked;
                if (timeoutIsSpecified()) {
                    Page.IsCheckedOptions options = new Page.IsCheckedOptions().setTimeout(timeout);
                    elementIsChecked = BrowseTheWebWithPlaywright.as(actor).getCurrentPage().isChecked(target.asSelector(), options);
                } else {
                    elementIsChecked = BrowseTheWebWithPlaywright.as(actor).getCurrentPage().isChecked(target.asSelector());
                }
                assertThat(elementIsChecked).withFailMessage("Expecting <%s> to be unchecked", target).isFalse();
            }
        );
    }

    /**
     * Check whether the element is enabled.
     */
    public Performable isEnabled() {
        return Task.where(target + " should be enabled",
            actor -> {
                boolean elementIsEnabled;
                if (timeoutIsSpecified()) {
                    Page.IsEnabledOptions options = new Page.IsEnabledOptions().setTimeout(timeout);
                    elementIsEnabled = BrowseTheWebWithPlaywright.as(actor).getCurrentPage().isEnabled(target.asSelector(), options);
                } else {
                    elementIsEnabled = BrowseTheWebWithPlaywright.as(actor).getCurrentPage().isEnabled(target.asSelector());
                }
                assertThat(elementIsEnabled).withFailMessage("Expecting <%s> to be enabled", target).isTrue();
            }
        );
    }

    /**
     * Check whether the element is disabled.
     */
    public Performable isDisabled() {
        return Task.where(target + " should be disabled",
            actor -> {
                boolean elementIsDisabled;
                if (timeoutIsSpecified()) {
                    Page.IsDisabledOptions options = new Page.IsDisabledOptions().setTimeout(timeout);
                    elementIsDisabled = BrowseTheWebWithPlaywright.as(actor).getCurrentPage().isDisabled(target.asSelector(), options);
                } else {
                    elementIsDisabled = BrowseTheWebWithPlaywright.as(actor).getCurrentPage().isDisabled(target.asSelector());
                }
                assertThat(elementIsDisabled).withFailMessage("Expecting <%s> to be disabled", target).isTrue();
            }
        );
    }

    /**
     * Check whether the element is editable.
     */
    public Performable isEditable() {
        return Task.where(target + " should be editable",
            actor -> {
                boolean elementIsEditable;
                if (timeoutIsSpecified()) {
                    Page.IsEditableOptions options = new Page.IsEditableOptions().setTimeout(timeout);
                    elementIsEditable = BrowseTheWebWithPlaywright.as(actor).getCurrentPage().isEditable(target.asSelector(), options);
                } else {
                    elementIsEditable = BrowseTheWebWithPlaywright.as(actor).getCurrentPage().isEditable(target.asSelector());
                }
                assertThat(elementIsEditable).withFailMessage("Expecting <%s> to be editable", target).isTrue();
            }
        );
    }
}
