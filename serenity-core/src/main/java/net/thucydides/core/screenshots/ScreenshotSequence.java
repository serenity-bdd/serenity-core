package net.thucydides.core.screenshots;

import java.util.concurrent.atomic.AtomicLong;

/**
 * A simple sequencer for screenshot numbers.
 * Numbers are guaranteed to be sequential.
 *
 */
public class ScreenshotSequence {
    private AtomicLong sequenceNumber = new AtomicLong(1);

    /**
     * Return the next number in the sequence.
     */
    public long next() {
        return sequenceNumber.getAndIncrement();
    }
}
