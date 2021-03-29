package net.serenitybdd.screenplay.playwright.assertions;


import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

import static org.assertj.core.api.Assertions.assertThat;

public class Ensure {

    private Target target;

    public Ensure(Target target) {
        this.target = target;
    }

    public static Ensure that(String selector) {
        return new Ensure(Target.the(selector).locatedBy(selector));
    }

    /**
     * Check whether the element is visible. A selector that does not match any elements is considered not visible.
     */
    public Performable isVisible() {
        return Task.where(target + " should be visible",
                actor -> {
                    assertThat(
                            BrowseTheWebWithPlaywright.as(actor).getCurrentPage().isVisible(target.asSelector())
                    ).withFailMessage("Expecting <%s> to be visible", target).isTrue();
                }
        );
    }

    /**
     * Check whether the element is not visible. A selector that does not match any elements is considered not visible.
     */
    public Performable isHidden() {
        return Task.where(target + " should be not visible",
                actor -> {
                    assertThat(
                            BrowseTheWebWithPlaywright.as(actor).getCurrentPage().isHidden(target.asSelector())
                    ).withFailMessage("Expecting <%s> to be hidden", target).isTrue();
                }
        );
    }

    /**
     * Check whether the element is checked. Throws if the element is not a checkbox or radio input.
     */
    public Performable isChecked() {
        return Task.where(target + " should be checked",
                actor -> {
                    assertThat(
                            BrowseTheWebWithPlaywright.as(actor).getCurrentPage().isChecked(target.asSelector())
                    ).withFailMessage("Expecting <%s> to be checked", target).isTrue();
                }
        );
    }

    /**
     * Check whether the element is NOT checked. Throws if the element is not a checkbox or radio input.
     */
    public Performable isNotChecked() {
        return Task.where(target + " should not be checked",
                actor -> {
                    assertThat(
                            BrowseTheWebWithPlaywright.as(actor).getCurrentPage().isChecked(target.asSelector())
                    ).withFailMessage("Expecting <%s> not to be checked", target).isFalse();
                }
        );
    }

}
