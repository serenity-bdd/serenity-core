package net.serenitybdd.model.time;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;

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
    public ZonedDateTime getCurrentTime() {
        return ZonedDateTime.now();
    }
}
