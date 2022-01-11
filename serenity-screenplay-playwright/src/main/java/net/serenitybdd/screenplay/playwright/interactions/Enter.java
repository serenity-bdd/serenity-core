package net.serenitybdd.screenplay.playwright.interactions;

import com.microsoft.playwright.Page;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;
import net.thucydides.core.annotations.Step;

/**
 * Enter a text value into an input field.
 * This task waits for an element matching selector, waits for actionability  checks, focuses the element,
 * fills it and triggers an input event after filling. If the element is inside the <label> element that has associated control,
 * that control will be filled instead.
 * If the element to be filled is not an <input>, <textarea> or [contenteditable] element, this method throws an error.
 * Note that you can pass an empty string to clear the input field.
 * <p>
 * Sample usage:
 * <pre>
 *     Enter.theValue("pengins").into("#searchfield");
 * </pre>
 * Or
 * <pre>
 *     Target SEARCH_FIELD = Target.the("Search field").locatedBy("#search_form_input_homepage")
 *     Enter.theValue("pengins").into(SEARCH_FIELD);
 * </pre>
 */
public class Enter implements Performable {

    /**
     * Default constructor required by Screenplay
     */
    public Enter() {
    }

    private String value;
    private Target target;
    private Page.FillOptions options;

    public Enter(String value) {
        this.value = value;
    }

    public static Enter theValue(String value) {
        return new Enter(value);
    }

    public Performable into(String selector) {
        this.target = Target.the(selector).locatedBy(selector);
        return this;
    }

    public Performable into(Target target) {
        this.target = target;
        return this;
    }

    public Performable withOptions(Page.FillOptions options) {
        this.options = options;
        return this;
    }

    @Override
    @Step("{0} enters #value into #target")
    public <T extends Actor> void performAs(T actor) {
        BrowseTheWebWithPlaywright.as(actor).getCurrentPage().fill(target.asSelector(), value, options);
        BrowseTheWebWithPlaywright.as(actor).notifyScreenChange();
    }
}
