package net.serenitybdd.screenplay.playwright.interactions.deselectactions;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

import java.util.Arrays;

/**
 * Deselect options from a multi-select dropdown by their value attributes.
 */
public class DeselectByValueFromTarget implements Performable {
    private final Target target;
    private final String[] values;

    public DeselectByValueFromTarget(Target target, String... values) {
        this.target = target;
        this.values = values;
    }

    @Step("{0} deselects values #values in #target")
    public <T extends Actor> void performAs(T theUser) {
        Page page = BrowseTheWebWithPlaywright.as(theUser).getCurrentPage();
        Locator locator = target.resolveFor(page);

        // Use JavaScript to deselect options by value
        String script = "el => {" +
                "const valuesToDeselect = " + toJsArray(values) + ";" +
                "Array.from(el.options).forEach(opt => {" +
                "  if (valuesToDeselect.includes(opt.value)) opt.selected = false;" +
                "});" +
                "el.dispatchEvent(new Event('change', { bubbles: true }));" +
                "}";
        locator.evaluate(script);
        BrowseTheWebWithPlaywright.as(theUser).notifyScreenChange();
    }

    private String toJsArray(String[] values) {
        return "[" + String.join(",", Arrays.stream(values)
                .map(v -> "\"" + v.replace("\"", "\\\"") + "\"")
                .toArray(String[]::new)) + "]";
    }
}
