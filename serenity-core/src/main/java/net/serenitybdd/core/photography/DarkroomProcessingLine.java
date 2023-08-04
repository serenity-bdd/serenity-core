package net.serenitybdd.core.photography;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class DarkroomProcessingLine implements Runnable {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    boolean openForBusiness;

    private final List<? extends PhotoFilter> processors;

    private final List<ScreenshotNegative> queue;


    public void terminate() {
        openForBusiness = false;

        synchronized (queue) {
            queue.notifyAll();
        }

    }

    DarkroomProcessingLine(List<? extends PhotoFilter> processors) {
        this.processors = processors;
        this.queue = Collections.synchronizedList(new LinkedList<>());
        this.openForBusiness = true;
    }

    public ScreenshotReceipt addToProcessingQueue(ScreenshotNegative negative) {
        queue.add(negative);
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
        while (openForBusiness) {
            synchronized (queue) {
                processNegative();
                try {
                    if (openForBusiness) {
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
            ScreenshotNegative negative = queue.remove(0);
            if (negative != null) {
                process(negative);
            }
        }
    }

    public void process(ScreenshotNegative negative) {
        ensureThatTheDarkroomIsStillOpen();

        Path screenshotPath = screenshotPathFor(negative);

        if (!Files.exists(screenshotPath)) {
            saveProcessedScreenshot(negative);
        }
        deleteTemporaryScreenshotFrom(negative);
    }

    private void deleteTemporaryScreenshotFrom(ScreenshotNegative negative) {
        try {
            Files.deleteIfExists(negative.getTemporaryPath());
        } catch (IOException e) {
            LOGGER.warn("Failed to delete temporary screenshot", e);
        }
    }

    private void saveProcessedScreenshot(ScreenshotNegative negative) {
        LOGGER.debug("Processing screenshot image in {}", negative.getTemporaryPath());
        for (PhotoFilter processor : processors) {
            negative = processor.process(negative);
        }
        try {
            LOGGER.debug("Saving screenshot to " + negative.getScreenshotPath());
            if (!Files.exists(negative.getScreenshotPath())) {
                ensureParentDirectoryExistsFor(negative.getScreenshotPath());
                Files.copy(negative.getTemporaryPath(), negative.getScreenshotPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (FileAlreadyExistsException noFurtherActionRequired) {
        } catch (IOException e) {
            LOGGER.warn("Failed to save screenshot", e);
        }
    }

    private void ensureParentDirectoryExistsFor(Path screenshotPath) throws IOException {
        if (screenshotPath.getParent() != null) {
            Files.createDirectories(screenshotPath.getParent());
        }

    }


    private Path screenshotPathFor(ScreenshotNegative negative) {
        ScreenshotNegative amendedNegative = negative;
        for (PhotoFilter processor : processors) {
            amendedNegative = amendedNegative.withScreenshotPath(processor.amendedScreenshotPath(amendedNegative));
        }
        return amendedNegative.getScreenshotPath();
    }

    private void ensureThatTheDarkroomIsStillOpen() {
        Preconditions.checkArgument(openForBusiness,"The darkroom is closed and cannot accept any more negatives");
    }
}
