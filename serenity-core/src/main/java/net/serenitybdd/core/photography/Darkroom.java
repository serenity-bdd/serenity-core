package net.serenitybdd.core.photography;

import net.serenitybdd.core.photography.bluring.Blurer;
import net.serenitybdd.core.photography.resizing.Resizer;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.model.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static net.thucydides.model.ThucydidesSystemProperty.SERENITY_COMPRESS_SCREENSHOTS;

/**
 * A darkroom processes and saves screenshots that were taken during the tests.
 */
public class Darkroom {

    private final static Logger LOGGER = LoggerFactory.getLogger(Darkroom.class);
    private final static List<PhotoFilter> DEFAULT_PROCESSORS = List.of(new Blurer());
    private DarkroomProcessingLine processingLine;
    private Thread screenshotThread;
    private final EnvironmentVariables environmentVariables;

    public void isOpenForBusiness() {
        synchronized (this) {
            if (theDarkroomIsClosed()) {
                LOGGER.debug("Opening darkroom");
                start();
            }
        }
    }

    public Darkroom() {
        this.environmentVariables = SystemEnvironmentVariables.currentEnvironmentVariables();
    }

    private boolean theDarkroomIsClosed() {
        return !theDarkroomIsOpen();
    }

    private List<? extends PhotoFilter> getProcessors() {
        List<PhotoFilter> processors = new ArrayList<>();
        if (SERENITY_COMPRESS_SCREENSHOTS.booleanFrom(environmentVariables, false)) {
            processors.add(new Resizer());
        }
        processors.addAll(DEFAULT_PROCESSORS);

        return processors;
    }

    private boolean theDarkroomIsOpen() {
        return (processingLine != null && processingLine.openForBusiness);
    }

    public void waitUntilClose() {
        LOGGER.debug("Closing darkroom");
        if (theDarkroomIsOpen()) {
            terminate();
        }
    }

    public void start() {
        if (theDarkroomIsOpen()) {
            return; // Already open, no need to start again.
        }
        this.processingLine = new DarkroomProcessingLine(getProcessors());
        screenshotThread = new Thread(processingLine,"Darkroom Processing Line");
        screenshotThread.setDaemon(true);
        screenshotThread.start();
    }

    public void terminate() {
        if (processingLine != null) {
            shutdownProcessingLine();
            processingLine = null; // Clear the processing line to ensure the darkroom is marked as closed.
        }
        DarkroomFileSystem.close();
    }

    public ScreenshotReceipt submitForProcessing(ScreenshotNegative negative) {
        ensureThatTheProcessingLineIsRunning();
        return processingLine.addToProcessingQueue(negative);
    }

    private void ensureThatTheProcessingLineIsRunning() {
        if (processingLine == null) {
            start();
        }
    }

    private void shutdownProcessingLine() {
        processingLine.terminate();
        try {
            screenshotThread.join();
        } catch (InterruptedException e) {
            LOGGER.error("Screenshot processing interrupted",e);
        }
    }

    /**
     * Returns a receipt of the screenshot negative with the definitive destination path
     */
    public ScreenshotReceipt sendNegative(ScreenshotNegative screenshotNegative) {
        LOGGER.debug("Send negative for processing for " + screenshotNegative.getScreenshotPath());
        return submitForProcessing(screenshotNegative);
    }
}
