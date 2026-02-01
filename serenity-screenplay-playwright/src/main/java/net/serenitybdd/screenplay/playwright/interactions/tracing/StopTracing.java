package net.serenitybdd.screenplay.playwright.interactions.tracing;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Tracing;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Stop Playwright tracing and save the trace file.
 * The trace can be viewed using the Playwright Trace Viewer:
 * {@code npx playwright show-trace trace.zip}
 *
 * <p>Sample usage:</p>
 * <pre>
 *     // Stop tracing and save to default location
 *     actor.attemptsTo(StopTracing.andSaveAs("my-trace"));
 *
 *     // Stop tracing and save to specific path
 *     actor.attemptsTo(StopTracing.andSaveTo(Paths.get("traces/checkout.zip")));
 * </pre>
 *
 * @see StartTracing
 * @see <a href="https://playwright.dev/java/docs/trace-viewer">Playwright Trace Viewer</a>
 */
public class StopTracing implements Performable {

    private static final String DEFAULT_TRACES_PATH = "target/playwright/traces";
    private final Path outputPath;

    private StopTracing(Path outputPath) {
        this.outputPath = outputPath;
    }

    /**
     * Stop tracing and save with the given name to the default traces directory.
     * The file will be saved as: target/playwright/traces/{name}.zip
     *
     * @param name The name for the trace file (without extension)
     */
    public static StopTracing andSaveAs(String name) {
        return new StopTracing(Paths.get(DEFAULT_TRACES_PATH, name + ".zip"));
    }

    /**
     * Stop tracing and save to the specified path.
     *
     * @param path The full path for the trace file (should end in .zip)
     */
    public static StopTracing andSaveTo(Path path) {
        return new StopTracing(path);
    }

    /**
     * Stop tracing and save to the specified path.
     *
     * @param path The full path for the trace file (should end in .zip)
     */
    public static StopTracing andSaveTo(String path) {
        return new StopTracing(Paths.get(path));
    }

    @Override
    @Step("{0} stops tracing and saves to #outputPath")
    public <T extends Actor> void performAs(T actor) {
        BrowseTheWebWithPlaywright ability = BrowseTheWebWithPlaywright.as(actor);
        BrowserContext context = ability.getBrowser().contexts().get(0);

        // Ensure directory exists
        outputPath.getParent().toFile().mkdirs();

        context.tracing().stop(new Tracing.StopOptions().setPath(outputPath));
    }
}
