package net.serenitybdd.core.photography;

import com.google.common.collect.ImmutableList;
import net.serenitybdd.core.photography.bluring.Blurer;
import net.serenitybdd.core.photography.resizing.Resizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Darkroom {

    private final static Logger LOGGER = LoggerFactory.getLogger(Darkroom.class);
    private List<? extends NegativeProcessor> processors = ImmutableList.of(new Resizer(), new Blurer());
    private DarkroomProcessingLine processingLine;
    private Thread screenshotThread;

    private static ThreadLocal<Darkroom> theDarkroom = new ThreadLocal();

    public static void isOpenForBusiness() {
        if (theDarkroomIsClosed()) {
            LOGGER.info("Opening darkroom");
            System.out.println("Opening darkroom");
            theDarkroom.set(new Darkroom());
        }
    }

    public Darkroom() {
        start();
    }

    private static boolean theDarkroomIsClosed() {
        return (theDarkroom.get() == null);
    }

    private static boolean theDarkroomIsOpen() {
        return (theDarkroom.get() != null);
    }

    public static void waitUntilClose() {
        LOGGER.info("Closing darkroom");
        System.out.println("Closing darkroom");
        if (theDarkroomIsOpen()) {
            theDarkroom.get().terminate();
            theDarkroom.remove();
        }
    }

    public void start() {
        this.processingLine = new DarkroomProcessingLine(processors);
        screenshotThread = new Thread(processingLine,"Darkroom Processing Line");
        screenshotThread.setDaemon(true);
        screenshotThread.start();
    }

    public void terminate() {
        processingLine.terminate();
        try {
            screenshotThread.join();
        } catch (InterruptedException e) {
            LOGGER.error("Screenshot processing interrupted",e);
        }
        DarkroomFileSystem.close();
    }

    public ScreenshotReceipt submitForProcessing(ScreenshotNegative negative) {
        return processingLine.addToProcessingQueue(negative);
    }

    /**
     * Returns a receipt of the screenshot negative with the definitive destination path
     */
    public static ScreenshotReceipt sendNegative(ScreenshotNegative screenshotNegative) {
        LOGGER.debug("Send negative for processing for " + screenshotNegative.getScreenshotPath());
        return theDarkroom.get().submitForProcessing(screenshotNegative);
    }
}
