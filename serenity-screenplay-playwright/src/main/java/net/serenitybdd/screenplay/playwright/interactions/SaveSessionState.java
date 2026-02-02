package net.serenitybdd.screenplay.playwright.interactions;

import com.microsoft.playwright.BrowserContext;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Save the current browser session state (cookies, localStorage, sessionStorage) to a file.
 * This allows reusing authentication state across tests, significantly speeding up test suites.
 *
 * <p>The saved state includes:</p>
 * <ul>
 *   <li>All cookies from the browser context</li>
 *   <li>localStorage data for all origins</li>
 *   <li>sessionStorage data for all origins</li>
 * </ul>
 *
 * <p>Sample usage:</p>
 * <pre>
 *     // After logging in, save the session state
 *     actor.attemptsTo(
 *         Navigate.to(loginPage),
 *         Enter.theValue("user@example.com").into("#email"),
 *         Enter.theValue("password").into("#password"),
 *         Click.on("#login-btn")
 *     );
 *
 *     // Save the authenticated state to a file
 *     actor.attemptsTo(SaveSessionState.toFile("auth-state.json"));
 *
 *     // In subsequent tests, restore the state to skip login
 *     actor.attemptsTo(RestoreSessionState.fromFile("auth-state.json"));
 * </pre>
 *
 * @see RestoreSessionState
 * @see <a href="https://playwright.dev/java/docs/auth">Playwright Authentication</a>
 */
public class SaveSessionState implements Performable {

    private static final String DEFAULT_STATE_DIRECTORY = "target/playwright/session-state";
    private final Path outputPath;

    private SaveSessionState(Path outputPath) {
        this.outputPath = outputPath;
    }

    /**
     * Save session state to a file with the given name in the default directory.
     * The file will be saved as: target/playwright/session-state/{name}.json
     *
     * @param name The name for the state file (without extension)
     */
    public static SaveSessionState toFile(String name) {
        return new SaveSessionState(Paths.get(DEFAULT_STATE_DIRECTORY, name + ".json"));
    }

    /**
     * Save session state to the specified path.
     *
     * @param path The full path for the state file
     */
    public static SaveSessionState toPath(Path path) {
        return new SaveSessionState(path);
    }

    /**
     * Save session state to the specified path.
     *
     * @param path The full path for the state file
     */
    public static SaveSessionState toPath(String path) {
        return new SaveSessionState(Paths.get(path));
    }

    @Override
    @Step("{0} saves session state to #outputPath")
    public <T extends Actor> void performAs(T actor) {
        BrowseTheWebWithPlaywright ability = BrowseTheWebWithPlaywright.as(actor);

        // Ensure the page exists (which creates the context)
        ability.getCurrentPage();

        // Get the browser context
        BrowserContext context = ability.getBrowser().contexts().get(0);

        // Ensure directory exists
        if (outputPath.getParent() != null) {
            outputPath.getParent().toFile().mkdirs();
        }

        // Save the storage state to file
        context.storageState(new BrowserContext.StorageStateOptions().setPath(outputPath));
    }

    /**
     * Get the path where the session state will be saved.
     */
    public Path getOutputPath() {
        return outputPath;
    }
}
