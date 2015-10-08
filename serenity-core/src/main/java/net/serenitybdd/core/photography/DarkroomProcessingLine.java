package net.serenitybdd.core.photography;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DarkroomProcessingLine implements Runnable {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    boolean done = false;

    private final List<? extends NegativeProcessor> processors;

    public void terminate() {
        done = true;
        synchronized (queue) {
            queue.notifyAll();
        }

    }

    private final Queue<ScreenshotNegative> queue;

    DarkroomProcessingLine(List<? extends NegativeProcessor> processors) {
        this.processors = processors;
        this.queue = new ConcurrentLinkedQueue<>();
    }

    public ScreenshotReceipt addToProcessingQueue(ScreenshotNegative negative) {
        queue.offer(negative);
        synchronized (queue) {
            queue.notifyAll();
        }
        return recieptFor(negative);
    }

    private ScreenshotReceipt recieptFor(ScreenshotNegative negative) {
        return new ScreenshotReceipt(screenshotPathFor(negative));
    }

    public void run() {
        LOGGER.debug("Darkroom processing line starting up");
        while (!done) {
            synchronized (queue) {
                processNegative();
                try {
                    if (!done) {
                        queue.wait();
                    } else {
                        finishProcessingNegatives();
                    }
                } catch (InterruptedException ignore) {
                }
            }
        }
        LOGGER.debug("Darkroom processing line shutting down");
    }

    private void finishProcessingNegatives() {
        processNegative();
    }

    private void processNegative() {
        while (!queue.isEmpty()) {
            ScreenshotNegative negative = queue.poll();
            if (negative != null) {
                process(negative);
            }
        }
    }

    public void process(ScreenshotNegative negative) {
        ensureThatTheDarkroomIsStillOpen();

        Path screenshotPath = screenshotPathFor(negative);

        if (Files.exists(screenshotPath)) {
            return;
        }
        LOGGER.debug("Processing screenshot image");
        for (NegativeProcessor processor : processors) {
            negative = processor.process(negative);
        }
        try {
            LOGGER.debug("Saving screenshot to " + negative.getScreenshotPath());
            if (!Files.exists(negative.getScreenshotPath())) {
                Files.copy(negative.getTemporaryPath(), negative.getScreenshotPath());
            }
            Files.deleteIfExists(negative.getTemporaryPath());
            LOGGER.debug("Screenshot saved");
        } catch (IOException e) {
            LOGGER.warn("Failed to save screenshot", e);
        }
    }

    private Path screenshotPathFor(ScreenshotNegative negative) {
        ScreenshotNegative amendedNegative = negative;
        for (NegativeProcessor processor : processors) {
            amendedNegative = amendedNegative.withScreenshotPath(processor.amendedScreenshotPath(amendedNegative));
        }
        return amendedNegative.getScreenshotPath();
    }

    private void ensureThatTheDarkroomIsStillOpen() {
        Preconditions.checkArgument(!done,"The darkroom is closed and cannot accept any more negatives");
    }
}