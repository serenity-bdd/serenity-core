package net.serenitybdd.screenplay.playwright.interactions.tracing;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Tracing;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

/**
 * Start Playwright tracing to capture screenshots, snapshots, and network activity.
 * Traces can be viewed using the Playwright Trace Viewer.
 *
 * <p>Sample usage:</p>
 * <pre>
 *     // Start tracing with default options
 *     actor.attemptsTo(StartTracing.withName("my-test"));
 *
 *     // Start tracing with all options enabled
 *     actor.attemptsTo(
 *         StartTracing.withName("checkout-flow")
 *             .withScreenshots()
 *             .withSnapshots()
 *             .withSources()
 *     );
 * </pre>
 *
 * @see StopTracing
 * @see <a href="https://playwright.dev/java/docs/trace-viewer">Playwright Trace Viewer</a>
 */
public class StartTracing implements Performable {

    private final String name;
    private boolean screenshots = true;
    private boolean snapshots = true;
    private boolean sources = false;
    private String title;

    private StartTracing(String name) {
        this.name = name;
    }

    /**
     * Start tracing with the given name.
     *
     * @param name The name for this trace (used in trace file)
     */
    public static StartTracing withName(String name) {
        return new StartTracing(name);
    }

    /**
     * Enable screenshot capture during tracing.
     */
    public StartTracing withScreenshots() {
        this.screenshots = true;
        return this;
    }

    /**
     * Disable screenshot capture during tracing.
     */
    public StartTracing withoutScreenshots() {
        this.screenshots = false;
        return this;
    }

    /**
     * Enable DOM snapshot capture during tracing.
     */
    public StartTracing withSnapshots() {
        this.snapshots = true;
        return this;
    }

    /**
     * Disable DOM snapshot capture during tracing.
     */
    public StartTracing withoutSnapshots() {
        this.snapshots = false;
        return this;
    }

    /**
     * Enable source code capture in traces.
     */
    public StartTracing withSources() {
        this.sources = true;
        return this;
    }

    /**
     * Set a display title for the trace.
     */
    public StartTracing withTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    @Step("{0} starts tracing with name '#name'")
    public <T extends Actor> void performAs(T actor) {
        BrowseTheWebWithPlaywright ability = BrowseTheWebWithPlaywright.as(actor);
        // Ensure page exists to get the context
        ability.getCurrentPage();
        BrowserContext context = ability.getBrowser().contexts().get(0);

        Tracing.StartOptions options = new Tracing.StartOptions()
            .setScreenshots(screenshots)
            .setSnapshots(snapshots)
            .setSources(sources)
            .setName(name);

        if (title != null) {
            options.setTitle(title);
        }

        context.tracing().start(options);
    }
}
