package net.serenitybdd.screenplay.playwright.interactions;

import com.microsoft.playwright.Download;
import com.microsoft.playwright.Page;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

import java.nio.file.Path;

/**
 * Wait for a file download while performing an action that triggers it.
 *
 * <p>Sample usage:</p>
 * <pre>
 *     // Wait for download while clicking a link
 *     actor.attemptsTo(
 *         WaitForDownload.whilePerforming(Click.on("#download-btn"))
 *             .andSaveAs("downloaded-file.pdf")
 *     );
 *
 *     // Wait for download and save to specific path
 *     actor.attemptsTo(
 *         WaitForDownload.whilePerforming(Click.on(DOWNLOAD_LINK))
 *             .andSaveTo(Paths.get("downloads/report.xlsx"))
 *     );
 *
 *     // Just wait for download without saving
 *     actor.attemptsTo(
 *         WaitForDownload.whilePerforming(Click.on("#export-btn"))
 *     );
 * </pre>
 *
 * <p>After performing this interaction, you can use {@link net.serenitybdd.screenplay.playwright.questions.DownloadedFile}
 * to query information about the most recent download.</p>
 *
 * @see net.serenitybdd.screenplay.playwright.questions.DownloadedFile
 */
public class WaitForDownload implements Performable {

    private static final String LAST_DOWNLOAD_KEY = "playwright.lastDownload";

    private final Performable triggeringAction;
    private Path savePath;
    private String saveFilename;
    private Double timeoutMs;

    private WaitForDownload(Performable triggeringAction) {
        this.triggeringAction = triggeringAction;
    }

    /**
     * Wait for a download while performing the specified action.
     *
     * @param action The action that triggers the download (e.g., clicking a download button)
     */
    public static WaitForDownload whilePerforming(Performable action) {
        return new WaitForDownload(action);
    }

    /**
     * Save the downloaded file with the specified filename in the default downloads directory.
     *
     * @param filename The filename to save as
     */
    public WaitForDownload andSaveAs(String filename) {
        this.saveFilename = filename;
        return this;
    }

    /**
     * Save the downloaded file to the specified path.
     *
     * @param path The full path where the file should be saved
     */
    public WaitForDownload andSaveTo(Path path) {
        this.savePath = path;
        return this;
    }

    /**
     * Set a custom timeout for waiting for the download.
     *
     * @param timeoutMs Timeout in milliseconds
     */
    public WaitForDownload withTimeout(double timeoutMs) {
        this.timeoutMs = timeoutMs;
        return this;
    }

    @Override
    @Step("{0} waits for download while performing action")
    public <T extends Actor> void performAs(T actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();

        Page.WaitForDownloadOptions options = new Page.WaitForDownloadOptions();
        if (timeoutMs != null) {
            options.setTimeout(timeoutMs);
        }

        Download download = page.waitForDownload(options, () -> {
            triggeringAction.performAs(actor);
        });

        // Store the download for later querying
        actor.remember(LAST_DOWNLOAD_KEY, download);

        // Save the file if requested
        if (savePath != null) {
            savePath.getParent().toFile().mkdirs();
            download.saveAs(savePath);
        } else if (saveFilename != null) {
            Path defaultPath = Path.of("target/downloads", saveFilename);
            defaultPath.getParent().toFile().mkdirs();
            download.saveAs(defaultPath);
        }

        BrowseTheWebWithPlaywright.as(actor).notifyScreenChange();
    }

    /**
     * Get the key used to store the last download in actor's memory.
     */
    public static String getLastDownloadKey() {
        return LAST_DOWNLOAD_KEY;
    }
}
