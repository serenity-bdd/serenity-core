package net.serenitybdd.playwright;

import com.microsoft.playwright.Page;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Thread-safe registry for Playwright Page instances.
 * <p>
 * This class provides ThreadLocal storage for Playwright Page objects,
 * enabling proper isolation in parallel test execution scenarios.
 * Each test thread maintains its own set of registered pages.
 * </p>
 * <p>
 * Usage:
 * <pre>
 * // Register a page for screenshot capture
 * PlaywrightPageRegistry.registerPage(page);
 *
 * // Get all registered pages for the current thread
 * List&lt;Page&gt; pages = PlaywrightPageRegistry.getRegisteredPages();
 *
 * // Cleanup at end of test
 * PlaywrightPageRegistry.clear();
 * </pre>
 * </p>
 */
public class PlaywrightPageRegistry {

    private static final ThreadLocal<List<Page>> registeredPages =
            ThreadLocal.withInitial(ArrayList::new);

    /**
     * Registers a Playwright Page instance for the current thread.
     * <p>
     * Once registered, the page will be used for automatic screenshot capture
     * during step execution. Null values and duplicate registrations are ignored.
     * </p>
     *
     * @param page the Playwright Page to register (may be null)
     */
    public static void registerPage(Page page) {
        if (page != null && !registeredPages.get().contains(page)) {
            registeredPages.get().add(page);
        }
    }

    /**
     * Unregisters a Playwright Page instance from the current thread.
     *
     * @param page the Playwright Page to unregister
     */
    public static void unregisterPage(Page page) {
        registeredPages.get().remove(page);
    }

    /**
     * Returns an unmodifiable list of all registered pages for the current thread.
     *
     * @return unmodifiable list of registered Page instances
     */
    public static List<Page> getRegisteredPages() {
        return Collections.unmodifiableList(new ArrayList<>(registeredPages.get()));
    }

    /**
     * Clears all registered pages for the current thread.
     * <p>
     * This should be called in test cleanup to ensure proper resource management.
     * </p>
     */
    public static void clear() {
        registeredPages.get().clear();
    }

    /**
     * Checks if there are any registered pages for the current thread.
     *
     * @return true if at least one page is registered, false otherwise
     */
    public static boolean hasRegisteredPages() {
        return !registeredPages.get().isEmpty();
    }
}
