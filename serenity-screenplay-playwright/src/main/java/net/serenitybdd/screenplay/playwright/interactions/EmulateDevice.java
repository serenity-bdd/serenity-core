package net.serenitybdd.screenplay.playwright.interactions;

import com.microsoft.playwright.Page;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

/**
 * Emulate different devices by setting viewport size, user agent, and device characteristics.
 *
 * <p>Sample usage:</p>
 * <pre>
 *     // Emulate predefined devices
 *     actor.attemptsTo(EmulateDevice.iPhone12());
 *     actor.attemptsTo(EmulateDevice.iPadPro());
 *     actor.attemptsTo(EmulateDevice.pixel5());
 *     actor.attemptsTo(EmulateDevice.galaxyS21());
 *
 *     // Set custom viewport
 *     actor.attemptsTo(EmulateDevice.withViewport(1920, 1080));
 *
 *     // Emulate mobile with touch
 *     actor.attemptsTo(
 *         EmulateDevice.withViewport(375, 667)
 *             .withDeviceScaleFactor(2)
 *             .asMobile()
 *     );
 *
 *     // Desktop viewport
 *     actor.attemptsTo(EmulateDevice.desktop());
 *     actor.attemptsTo(EmulateDevice.desktopHD());
 *     actor.attemptsTo(EmulateDevice.desktop4K());
 * </pre>
 *
 * <p>Note: For full device emulation including user agent and other properties,
 * configure the browser context when creating the actor's ability.</p>
 */
public class EmulateDevice implements Performable {

    private final int width;
    private final int height;
    private Double deviceScaleFactor;
    private Boolean isMobile;
    private Boolean hasTouch;
    private final String deviceName;

    private EmulateDevice(int width, int height, String deviceName) {
        this.width = width;
        this.height = height;
        this.deviceName = deviceName;
    }

    /**
     * Set a custom viewport size.
     *
     * @param width Viewport width in pixels
     * @param height Viewport height in pixels
     */
    public static EmulateDevice withViewport(int width, int height) {
        return new EmulateDevice(width, height, "custom viewport " + width + "x" + height);
    }

    // Predefined mobile devices

    /**
     * Emulate iPhone 12 / 12 Pro viewport.
     */
    public static EmulateDevice iPhone12() {
        return new EmulateDevice(390, 844, "iPhone 12")
            .withDeviceScaleFactor(3)
            .asMobile();
    }

    /**
     * Emulate iPhone 14 Pro viewport.
     */
    public static EmulateDevice iPhone14Pro() {
        return new EmulateDevice(393, 852, "iPhone 14 Pro")
            .withDeviceScaleFactor(3)
            .asMobile();
    }

    /**
     * Emulate iPhone SE viewport.
     */
    public static EmulateDevice iPhoneSE() {
        return new EmulateDevice(375, 667, "iPhone SE")
            .withDeviceScaleFactor(2)
            .asMobile();
    }

    /**
     * Emulate iPad viewport.
     */
    public static EmulateDevice iPad() {
        return new EmulateDevice(768, 1024, "iPad")
            .withDeviceScaleFactor(2)
            .asMobile();
    }

    /**
     * Emulate iPad Pro 11" viewport.
     */
    public static EmulateDevice iPadPro() {
        return new EmulateDevice(834, 1194, "iPad Pro")
            .withDeviceScaleFactor(2)
            .asMobile();
    }

    /**
     * Emulate Google Pixel 5 viewport.
     */
    public static EmulateDevice pixel5() {
        return new EmulateDevice(393, 851, "Pixel 5")
            .withDeviceScaleFactor(2.75)
            .asMobile();
    }

    /**
     * Emulate Google Pixel 7 viewport.
     */
    public static EmulateDevice pixel7() {
        return new EmulateDevice(412, 915, "Pixel 7")
            .withDeviceScaleFactor(2.625)
            .asMobile();
    }

    /**
     * Emulate Samsung Galaxy S21 viewport.
     */
    public static EmulateDevice galaxyS21() {
        return new EmulateDevice(360, 800, "Galaxy S21")
            .withDeviceScaleFactor(3)
            .asMobile();
    }

    /**
     * Emulate Samsung Galaxy Tab S7 viewport.
     */
    public static EmulateDevice galaxyTabS7() {
        return new EmulateDevice(800, 1280, "Galaxy Tab S7")
            .withDeviceScaleFactor(2)
            .asMobile();
    }

    // Desktop presets

    /**
     * Standard desktop viewport (1280x720).
     */
    public static EmulateDevice desktop() {
        return new EmulateDevice(1280, 720, "Desktop");
    }

    /**
     * HD desktop viewport (1920x1080).
     */
    public static EmulateDevice desktopHD() {
        return new EmulateDevice(1920, 1080, "Desktop HD");
    }

    /**
     * 4K desktop viewport (3840x2160).
     */
    public static EmulateDevice desktop4K() {
        return new EmulateDevice(3840, 2160, "Desktop 4K");
    }

    /**
     * Laptop viewport (1366x768).
     */
    public static EmulateDevice laptop() {
        return new EmulateDevice(1366, 768, "Laptop");
    }

    /**
     * MacBook Pro 14" viewport (1512x982).
     */
    public static EmulateDevice macBookPro14() {
        return new EmulateDevice(1512, 982, "MacBook Pro 14")
            .withDeviceScaleFactor(2);
    }

    /**
     * Set the device scale factor (pixel density).
     *
     * @param factor The device pixel ratio (e.g., 2 for Retina displays)
     */
    public EmulateDevice withDeviceScaleFactor(double factor) {
        this.deviceScaleFactor = factor;
        return this;
    }

    /**
     * Mark as a mobile device (enables mobile-specific behaviors).
     */
    public EmulateDevice asMobile() {
        this.isMobile = true;
        this.hasTouch = true;
        return this;
    }

    /**
     * Enable touch events.
     */
    public EmulateDevice withTouch() {
        this.hasTouch = true;
        return this;
    }

    @Override
    @Step("{0} emulates #deviceName")
    public <T extends Actor> void performAs(T actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();

        // Set viewport size
        page.setViewportSize(width, height);

        // Note: deviceScaleFactor, isMobile, and hasTouch are set at context creation time
        // in Playwright. For runtime changes, we can only change viewport.
        // For full device emulation, users should configure BrowseTheWebWithPlaywright
        // with appropriate context options.

        BrowseTheWebWithPlaywright.as(actor).notifyScreenChange();
    }

    /**
     * Get the viewport width.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Get the viewport height.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Get the device scale factor (if set).
     */
    public Double getDeviceScaleFactor() {
        return deviceScaleFactor;
    }

    /**
     * Check if this is configured as a mobile device.
     */
    public Boolean isMobile() {
        return isMobile;
    }
}
