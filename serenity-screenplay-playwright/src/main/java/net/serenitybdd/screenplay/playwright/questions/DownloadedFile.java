package net.serenitybdd.screenplay.playwright.questions;

import com.microsoft.playwright.Download;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.playwright.interactions.WaitForDownload;

import java.nio.file.Path;

/**
 * Query information about the most recently downloaded file.
 *
 * <p>This question is used after a {@link WaitForDownload} interaction
 * to retrieve details about the downloaded file.</p>
 *
 * <p>Sample usage:</p>
 * <pre>
 *     // First, perform the download
 *     actor.attemptsTo(
 *         WaitForDownload.whilePerforming(Click.on("#download-btn"))
 *     );
 *
 *     // Then query the download details
 *     String filename = actor.asksFor(DownloadedFile.suggestedFilename());
 *     String url = actor.asksFor(DownloadedFile.url());
 *     Path path = actor.asksFor(DownloadedFile.path());
 *
 *     // Check if download failed
 *     String error = actor.asksFor(DownloadedFile.failure());
 *     if (error == null) {
 *         // Download succeeded
 *     }
 * </pre>
 *
 * @see WaitForDownload
 */
public class DownloadedFile {

    private DownloadedFile() {
        // Factory class - prevent instantiation
    }

    /**
     * Get the suggested filename from the Content-Disposition header or URL.
     */
    public static Question<String> suggestedFilename() {
        return new SuggestedFilenameQuestion();
    }

    /**
     * Get the URL that initiated the download.
     */
    public static Question<String> url() {
        return new DownloadUrlQuestion();
    }

    /**
     * Get the path to the downloaded file.
     * Note: This is the temporary path; use WaitForDownload.andSaveTo() to save permanently.
     */
    public static Question<Path> path() {
        return new DownloadPathQuestion();
    }

    /**
     * Get the failure reason if the download failed, or null if successful.
     */
    public static Question<String> failure() {
        return new DownloadFailureQuestion();
    }

    /**
     * Get the Download object for advanced operations.
     */
    public static Question<Download> download() {
        return new DownloadObjectQuestion();
    }

    private static Download getLastDownload(Actor actor) {
        Download download = actor.recall(WaitForDownload.getLastDownloadKey());
        if (download == null) {
            throw new IllegalStateException(
                "No download found. Use WaitForDownload.whilePerforming() before querying download information."
            );
        }
        return download;
    }

    static class SuggestedFilenameQuestion implements Question<String> {
        @Override
        public String answeredBy(Actor actor) {
            return getLastDownload(actor).suggestedFilename();
        }

        @Override
        public String toString() {
            return "suggested filename of the downloaded file";
        }
    }

    static class DownloadUrlQuestion implements Question<String> {
        @Override
        public String answeredBy(Actor actor) {
            return getLastDownload(actor).url();
        }

        @Override
        public String toString() {
            return "URL of the downloaded file";
        }
    }

    static class DownloadPathQuestion implements Question<Path> {
        @Override
        public Path answeredBy(Actor actor) {
            return getLastDownload(actor).path();
        }

        @Override
        public String toString() {
            return "path of the downloaded file";
        }
    }

    static class DownloadFailureQuestion implements Question<String> {
        @Override
        public String answeredBy(Actor actor) {
            return getLastDownload(actor).failure();
        }

        @Override
        public String toString() {
            return "failure reason of the download (null if successful)";
        }
    }

    static class DownloadObjectQuestion implements Question<Download> {
        @Override
        public Download answeredBy(Actor actor) {
            return getLastDownload(actor);
        }

        @Override
        public String toString() {
            return "the downloaded file object";
        }
    }
}
