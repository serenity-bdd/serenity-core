package net.serenitybdd.screenplay.actions;

import net.serenitybdd.markers.CanBeSilent;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;

import java.util.function.Consumer;

/**
 * Perform an action directly with the Serenity WebDriver API.
 * For example:
 * <pre>
 *     <code>
 *         actor.attemptsTo(
 *             InTheBrowser.perform(
 *                 browser -> browser.evaluateJavascript("window.localStorage.clear()")
 *             )
 *         );
 *     </code>
 * </pre>
 *
 * You can access the Driver instance directly using browser.getDriver().
 */
public class InTheBrowser implements Performable, CanBeSilent {

    private final Consumer<BrowseTheWeb> action;
    private boolean isSilent = false;

    public InTheBrowser(Consumer<BrowseTheWeb> action) {
        this.action = action;
    }

    public static InTheBrowser perform(Consumer<BrowseTheWeb> action) {
        return new InTheBrowser(action);
    }


    @Override
    public <T extends Actor> void performAs(T actor) {
        action.accept(BrowseTheWeb.as(actor));
    }

    @Override
    public boolean isSilent() {
        return isSilent;
    }

    public Performable withNoReporting() {
        this.isSilent = true;
        return this;
    }
}
