package serenitycore.net.serenitybdd.core.photography;

import serenitycore.net.serenitybdd.core.photography.bluring.Blurer;
import serenitycore.net.serenitybdd.core.photography.resizing.Resizer;
import serenitymodel.net.thucydides.core.guice.Injectors;
import serenitymodel.net.thucydides.core.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static serenitymodel.net.thucydides.core.ThucydidesSystemProperty.SERENITY_COMPRESS_SCREENSHOTS;

public class Darkroom {

    private final static Logger LOGGER = LoggerFactory.getLogger(Darkroom.class);
    private final static List<PhotoFilter> DEFAULT_PROCESSORS = Arrays.asList(new Blurer());
    private DarkroomProcessingLine processingLine;
    private Thread screenshotThread;
    private final EnvironmentVariables environmentVariables;

    public void isOpenForBusiness() {
        if (theDarkroomIsClosed()) {
            LOGGER.debug("Opening darkroom");
            start();
        }
    }

    public Darkroom() {
        this.environmentVariables = Injectors.getInjector().getInstance(EnvironmentVariables.class);
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

        this.processingLine = new DarkroomProcessingLine(getProcessors());
        screenshotThread = new Thread(processingLine,"Darkroom Processing Line");
        screenshotThread.setDaemon(true);
        screenshotThread.start();
    }

    public void terminate() {
        if (processingLine != null) {
            shutdownProcessingLine();
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
