package net.serenitybdd.screenplay.playwright.interactions;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.options.Geolocation;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

/**
 * Set the browser's geolocation for testing location-based features.
 *
 * <p>Sample usage:</p>
 * <pre>
 *     // Set specific coordinates
 *     actor.attemptsTo(SetGeolocation.to(51.5074, -0.1278));  // London
 *
 *     // Set with accuracy
 *     actor.attemptsTo(SetGeolocation.to(40.7128, -74.0060).withAccuracy(100));  // New York
 *
 *     // Use predefined locations
 *     actor.attemptsTo(SetGeolocation.toNewYork());
 *     actor.attemptsTo(SetGeolocation.toLondon());
 *     actor.attemptsTo(SetGeolocation.toTokyo());
 *
 *     // Clear geolocation
 *     actor.attemptsTo(SetGeolocation.clear());
 * </pre>
 *
 * <p>Note: You must also grant the 'geolocation' permission for this to work.
 * See {@link GrantPermissions}.</p>
 *
 * @see GrantPermissions
 */
public class SetGeolocation implements Performable {

    private final Double latitude;
    private final Double longitude;
    private Double accuracy;
    private final boolean clear;

    private SetGeolocation(Double latitude, Double longitude, boolean clear) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.clear = clear;
    }

    /**
     * Set geolocation to the specified coordinates.
     *
     * @param latitude Latitude between -90 and 90
     * @param longitude Longitude between -180 and 180
     */
    public static SetGeolocation to(double latitude, double longitude) {
        return new SetGeolocation(latitude, longitude, false);
    }

    /**
     * Clear the geolocation override.
     */
    public static SetGeolocation clear() {
        return new SetGeolocation(null, null, true);
    }

    // Predefined locations

    /**
     * Set geolocation to New York City.
     */
    public static SetGeolocation toNewYork() {
        return to(40.7128, -74.0060);
    }

    /**
     * Set geolocation to London.
     */
    public static SetGeolocation toLondon() {
        return to(51.5074, -0.1278);
    }

    /**
     * Set geolocation to Tokyo.
     */
    public static SetGeolocation toTokyo() {
        return to(35.6762, 139.6503);
    }

    /**
     * Set geolocation to San Francisco.
     */
    public static SetGeolocation toSanFrancisco() {
        return to(37.7749, -122.4194);
    }

    /**
     * Set geolocation to Sydney.
     */
    public static SetGeolocation toSydney() {
        return to(-33.8688, 151.2093);
    }

    /**
     * Set geolocation to Paris.
     */
    public static SetGeolocation toParis() {
        return to(48.8566, 2.3522);
    }

    /**
     * Set the accuracy of the geolocation in meters.
     *
     * @param accuracy Accuracy in meters (default is 0)
     */
    public SetGeolocation withAccuracy(double accuracy) {
        this.accuracy = accuracy;
        return this;
    }

    @Override
    @Step("{0} sets geolocation to (#latitude, #longitude)")
    public <T extends Actor> void performAs(T actor) {
        BrowseTheWebWithPlaywright ability = BrowseTheWebWithPlaywright.as(actor);
        // Ensure page exists to get context
        ability.getCurrentPage();
        BrowserContext context = ability.getBrowser().contexts().get(0);

        if (clear) {
            context.setGeolocation(null);
        } else {
            Geolocation geolocation = new Geolocation(latitude, longitude);
            if (accuracy != null) {
                geolocation.accuracy = accuracy;
            }
            context.setGeolocation(geolocation);
        }
    }
}
