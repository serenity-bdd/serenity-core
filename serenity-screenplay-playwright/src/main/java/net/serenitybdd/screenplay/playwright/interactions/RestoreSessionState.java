package net.serenitybdd.screenplay.playwright.interactions;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.playwright.PlaywrightSerenity;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Restore browser session state (cookies, localStorage, sessionStorage) from a previously saved file.
 * This allows skipping login flows by reusing authentication state across tests.
 *
 * <p>The restored state includes:</p>
 * <ul>
 *   <li>All cookies from the saved context</li>
 *   <li>localStorage data for all origins</li>
 *   <li>sessionStorage data for all origins</li>
 * </ul>
 *
 * <p>Sample usage:</p>
 * <pre>
 *     // In a setup class or @BeforeAll, save auth state once:
 *     actor.attemptsTo(
 *         Navigate.to(loginPage),
 *         Enter.theValue("user@example.com").into("#email"),
 *         Enter.theValue("password").into("#password"),
 *         Click.on("#login-btn"),
 *         SaveSessionState.toFile("auth-state")
 *     );
 *
 *     // In subsequent tests, restore the state to skip login:
 *     actor.attemptsTo(RestoreSessionState.fromFile("auth-state"));
 *
 *     // Actor is now authenticated - navigate directly to protected pages
 *     actor.attemptsTo(Navigate.to(dashboardPage));
 * </pre>
 *
 * <p><strong>Important:</strong> This interaction creates a new browser context with the saved state.
 * Any existing pages will be closed. Call this early in your test setup.</p>
 *
 * @see SaveSessionState
 * @see <a href="https://playwright.dev/java/docs/auth">Playwright Authentication</a>
 */
public class RestoreSessionState implements Performable {

    private static final String DEFAULT_STATE_DIRECTORY = "target/playwright/session-state";
    private final Path inputPath;

    private RestoreSessionState(Path inputPath) {
        this.inputPath = inputPath;
    }

    /**
     * Restore session state from a file with the given name in the default directory.
     * The file should exist at: target/playwright/session-state/{name}.json
     *
     * @param name The name of the state file (without extension)
     */
    public static RestoreSessionState fromFile(String name) {
        return new RestoreSessionState(Paths.get(DEFAULT_STATE_DIRECTORY, name + ".json"));
    }

    /**
     * Restore session state from the specified path.
     *
     * @param path The full path to the state file
     */
    public static RestoreSessionState fromPath(Path path) {
        return new RestoreSessionState(path);
    }

    /**
     * Restore session state from the specified path.
     *
     * @param path The full path to the state file
     */
    public static RestoreSessionState fromPath(String path) {
        return new RestoreSessionState(Paths.get(path));
    }

    @Override
    @Step("{0} restores session state from #inputPath")
    public <T extends Actor> void performAs(T actor) {
        if (!Files.exists(inputPath)) {
            throw new IllegalStateException(
                "Session state file not found: " + inputPath +
                ". Use SaveSessionState.toFile() to create it first."
            );
        }

        BrowseTheWebWithPlaywright ability = BrowseTheWebWithPlaywright.as(actor);
        Browser browser = ability.getBrowser();

        // Close existing context and pages if any
        if (!browser.contexts().isEmpty()) {
            for (BrowserContext existingContext : browser.contexts()) {
                for (Page page : existingContext.pages()) {
                    PlaywrightSerenity.unregisterPage(page);
                }
                existingContext.close();
            }
        }

        // Create new context with the saved storage state
        Browser.NewContextOptions contextOptions = new Browser.NewContextOptions()
            .setStorageStatePath(inputPath);

        BrowserContext newContext = browser.newContext(contextOptions);

        // Create a new page in the restored context
        Page newPage = newContext.newPage();
        PlaywrightSerenity.registerPage(newPage);

        // Update the ability to use the new context and page
        // Note: This requires accessing internal state, so we use reflection or a helper method
        ability.setRestoredContext(newContext, newPage);
    }

    /**
     * Get the path from which the session state will be restored.
     */
    public Path getInputPath() {
        return inputPath;
    }
}
