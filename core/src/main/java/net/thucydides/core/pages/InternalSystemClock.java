package net.thucydides.core.pages;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Object that encapsulates system clock operations.
 */
public class InternalSystemClock implements SystemClock {

    private static final Logger LOGGER = LoggerFactory.getLogger(InternalSystemClock.class);

    /**
     * Pause execution for the requested delay.
     * Throws a runtime exception if something goes wrong.
     */
    public void pauseFor(final long timeInMilliseconds) {

         try {
             sleepFor(timeInMilliseconds);
         } catch (InterruptedException e) {
             LOGGER.error("Wait interrupted:" +  e.getMessage());
             throw new RuntimeException("System timer interrupted", e);
         }
    }

    protected void sleepFor(long timeInMilliseconds) throws InterruptedException {
        Thread.sleep(timeInMilliseconds);
    }

    /**
     * Find the current system time.
     */
    public DateTime getCurrentTime() {
        return new DateTime();
    }
}
