package net.serenitybdd.screenplay.playwright.interactions;

import com.microsoft.playwright.BrowserContext;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

import java.util.Arrays;
import java.util.List;

/**
 * Grant browser permissions for testing features that require user consent.
 *
 * <p>Sample usage:</p>
 * <pre>
 *     // Grant single permission
 *     actor.attemptsTo(GrantPermissions.for_("geolocation"));
 *
 *     // Grant multiple permissions
 *     actor.attemptsTo(GrantPermissions.for_("geolocation", "notifications", "camera"));
 *
 *     // Grant permissions for specific origin
 *     actor.attemptsTo(
 *         GrantPermissions.for_("clipboard-read", "clipboard-write")
 *             .onOrigin("https://example.com")
 *     );
 *
 *     // Use predefined permission sets
 *     actor.attemptsTo(GrantPermissions.forGeolocation());
 *     actor.attemptsTo(GrantPermissions.forNotifications());
 *     actor.attemptsTo(GrantPermissions.forMediaDevices());
 * </pre>
 *
 * <p>Available permissions include:</p>
 * <ul>
 *   <li>geolocation</li>
 *   <li>notifications</li>
 *   <li>camera</li>
 *   <li>microphone</li>
 *   <li>clipboard-read</li>
 *   <li>clipboard-write</li>
 *   <li>payment-handler</li>
 *   <li>midi</li>
 *   <li>midi-sysex</li>
 *   <li>ambient-light-sensor</li>
 *   <li>accelerometer</li>
 *   <li>gyroscope</li>
 *   <li>magnetometer</li>
 *   <li>accessibility-events</li>
 *   <li>background-sync</li>
 * </ul>
 *
 * @see SetGeolocation
 * @see ClearPermissions
 */
public class GrantPermissions implements Performable {

    private final List<String> permissions;
    private String origin;

    private GrantPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    /**
     * Grant the specified permissions.
     * Note: Using "for_" to avoid conflict with Java reserved word "for".
     *
     * @param permissions The permissions to grant
     */
    public static GrantPermissions for_(String... permissions) {
        return new GrantPermissions(Arrays.asList(permissions));
    }

    /**
     * Grant geolocation permission.
     */
    public static GrantPermissions forGeolocation() {
        return for_("geolocation");
    }

    /**
     * Grant notifications permission.
     */
    public static GrantPermissions forNotifications() {
        return for_("notifications");
    }

    /**
     * Grant camera and microphone permissions for media access.
     */
    public static GrantPermissions forMediaDevices() {
        return for_("camera", "microphone");
    }

    /**
     * Grant clipboard read and write permissions.
     */
    public static GrantPermissions forClipboard() {
        return for_("clipboard-read", "clipboard-write");
    }

    /**
     * Restrict the permissions to a specific origin.
     *
     * @param origin The origin URL (e.g., "https://example.com")
     */
    public GrantPermissions onOrigin(String origin) {
        this.origin = origin;
        return this;
    }

    @Override
    @Step("{0} grants permissions: #permissions")
    public <T extends Actor> void performAs(T actor) {
        BrowseTheWebWithPlaywright ability = BrowseTheWebWithPlaywright.as(actor);
        // Ensure page exists to get context
        ability.getCurrentPage();
        BrowserContext context = ability.getBrowser().contexts().get(0);

        BrowserContext.GrantPermissionsOptions options = new BrowserContext.GrantPermissionsOptions();
        if (origin != null) {
            options.setOrigin(origin);
        }

        context.grantPermissions(permissions, options);
    }
}
