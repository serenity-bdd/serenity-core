package net.serenitybdd.screenplay.playwright.interactions;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

/**
 * Right-click (context click) on an element.
 *
 * <p>Sample usage:</p>
 * <pre>
 *     actor.attemptsTo(
 *         RightClick.on("#element"),
 *         RightClick.on(MY_TARGET)
 *     );
 * </pre>
 */
public class RightClick implements Performable {

    private final Target target;
    private Locator.ClickOptions options;

    public RightClick(Target target) {
        this.target = target;
    }

    /**
     * Right-click on an element identified by a selector.
     */
    public static RightClick on(String selector) {
        return new RightClick(Target.the(selector).locatedBy(selector));
    }

    /**
     * Right-click on a Target element.
     */
    public static RightClick on(Target target) {
        return new RightClick(target);
    }

    /**
     * Provide additional click options.
     */
    public RightClick withOptions(Locator.ClickOptions options) {
        this.options = options;
        return this;
    }

    @Override
    @Step("{0} right-clicks on #target")
    public <T extends Actor> void performAs(T actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        Locator.ClickOptions clickOptions = options != null ? options : new Locator.ClickOptions();
        clickOptions.setButton(com.microsoft.playwright.options.MouseButton.RIGHT);
        target.resolveFor(page).click(clickOptions);
        BrowseTheWebWithPlaywright.as(actor).notifyScreenChange();
    }
}
