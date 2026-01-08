package net.serenitybdd.playwright;

import com.microsoft.playwright.Page;
import net.thucydides.core.steps.StepEventBus;

import java.util.List;

/**
 * Main entry point for Playwright integration with Serenity BDD.
 * <p>
 * This class provides a simple API for registering Playwright Page instances
 * with Serenity, enabling automatic screenshot capture during step execution.
 * </p>
 * <p>
 * Basic usage:
 * <pre>
 * // In your test setup
 * Page page = browser.newPage();
 * PlaywrightSerenity.registerPage(page);
 *
 * // Run your tests with @Step annotated methods
 * // Screenshots will be automatically captured
 *
 * // In your test cleanup
 * PlaywrightSerenity.unregisterPage(page);
 * </pre>
 * </p>
 *
 * @see PlaywrightPageRegistry
 * @see PlaywrightPhotoLens
 */
public class PlaywrightSerenity {

    // ThreadLocal to track if listener is registered for the current thread's event bus
    private static final ThreadLocal<Boolean> listenerRegisteredForThread =
            ThreadLocal.withInitial(() -> false);

    /**
     * Registers a Playwright Page instance for screenshot capture.
     * <p>
     * Once registered, screenshots will be automatically captured for this page
     * at the completion of each @Step annotated method.
     * </p>
     *
     * @param page the Playwright Page to register
     */
    public static void registerPage(Page page) {
        PlaywrightPageRegistry.registerPage(page);
        ensureStepListenerRegistered();
    }

    /**
     * Unregisters a Playwright Page instance.
     * <p>
     * Call this in your test cleanup to stop screenshot capture for this page.
     * </p>
     *
     * @param page the Playwright Page to unregister
     */
    public static void unregisterPage(Page page) {
        PlaywrightPageRegistry.unregisterPage(page);
        // Reset listener flag when no more pages are registered
        // This ensures a fresh listener is registered for the next test's event bus
        if (!PlaywrightPageRegistry.hasRegisteredPages()) {
            listenerRegisteredForThread.set(false);
        }
    }

    /**
     * Returns the most recently registered Page for the current thread.
     * <p>
     * This is useful in step library methods that need access to the Page
     * without having it passed as a parameter.
     * </p>
     *
     * @return the most recently registered Page, or null if none registered
     */
    public static Page getCurrentPage() {
        List<Page> pages = PlaywrightPageRegistry.getRegisteredPages();
        return pages.isEmpty() ? null : pages.get(pages.size() - 1);
    }

    /**
     * Returns all registered pages for the current thread.
     *
     * @return unmodifiable list of registered Page instances
     */
    public static List<Page> getRegisteredPages() {
        return PlaywrightPageRegistry.getRegisteredPages();
    }

    /**
     * Clears all registered pages for the current thread.
     * <p>
     * Call this in your test cleanup to ensure proper resource management.
     * </p>
     */
    public static void clear() {
        PlaywrightPageRegistry.clear();
        listenerRegisteredForThread.set(false);
    }

    /**
     * Manually triggers screenshot capture for all registered pages.
     * <p>
     * Screenshots are normally captured automatically at step boundaries.
     * This method allows explicit capture at any point in a test.
     * </p>
     */
    public static void takeScreenshot() {
        if (!PlaywrightPageRegistry.hasRegisteredPages()) {
            return;
        }

        PlaywrightStepListener listener = new PlaywrightStepListener();
        listener.takeScreenshotNow();
    }

    private static void ensureStepListenerRegistered() {
        if (!listenerRegisteredForThread.get()) {
            StepEventBus eventBus = StepEventBus.getParallelEventBus();
            PlaywrightStepListener listener = new PlaywrightStepListener();
            eventBus.registerListener(listener);
            listenerRegisteredForThread.set(true);
        }
    }

    /**
     * Resets the internal state. Intended for testing purposes only.
     */
    static void resetForTesting() {
        listenerRegisteredForThread.set(false);
        PlaywrightPageRegistry.clear();
    }
}
