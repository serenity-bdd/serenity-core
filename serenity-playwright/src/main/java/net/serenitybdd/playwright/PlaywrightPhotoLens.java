package net.serenitybdd.playwright;

import com.microsoft.playwright.Page;
import net.serenitybdd.core.photography.PhotoLens;

/**
 * A PhotoLens implementation for Playwright Page screenshots.
 * <p>
 * This class implements the Serenity {@link PhotoLens} interface to enable
 * Playwright-based screenshot capture integration with Serenity's photography system.
 * </p>
 * <p>
 * Screenshots are captured as full-page PNG images by default.
 * </p>
 */
public class PlaywrightPhotoLens implements PhotoLens {

    private final Page page;

    /**
     * Creates a new PlaywrightPhotoLens for the given Page.
     *
     * @param page the Playwright Page to use for screenshots (may be null)
     */
    public PlaywrightPhotoLens(Page page) {
        this.page = page;
    }

    /**
     * Takes a screenshot of the current page state.
     *
     * @return the screenshot as a byte array, or empty array if screenshot cannot be taken
     */
    @Override
    public byte[] takeScreenshot() {
        if (!canTakeScreenshot()) {
            return new byte[0];
        }

        try {
            Page.ScreenshotOptions options = new Page.ScreenshotOptions()
                    .setFullPage(true);
            return page.screenshot(options);
        } catch (Exception e) {
            return new byte[0];
        }
    }

    /**
     * Checks if a screenshot can be taken.
     *
     * @return true if the page is available and not closed, false otherwise
     */
    @Override
    public boolean canTakeScreenshot() {
        try {
            return page != null && !page.isClosed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns the underlying Playwright Page.
     *
     * @return the Page instance, or null if not set
     */
    public Page getPage() {
        return page;
    }
}
