package net.serenitybdd.screenplay.playwright.interactions;

import com.microsoft.playwright.BrowserContext;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

/**
 * Clear all previously granted browser permissions.
 *
 * <p>Sample usage:</p>
 * <pre>
 *     // Clear all permissions
 *     actor.attemptsTo(ClearPermissions.all());
 * </pre>
 *
 * @see GrantPermissions
 */
public class ClearPermissions implements Performable {

    private ClearPermissions() {
    }

    /**
     * Clear all granted permissions.
     */
    public static ClearPermissions all() {
        return new ClearPermissions();
    }

    @Override
    @Step("{0} clears all permissions")
    public <T extends Actor> void performAs(T actor) {
        BrowseTheWebWithPlaywright ability = BrowseTheWebWithPlaywright.as(actor);
        // Ensure page exists to get context
        ability.getCurrentPage();
        BrowserContext context = ability.getBrowser().contexts().get(0);
        context.clearPermissions();
    }
}
