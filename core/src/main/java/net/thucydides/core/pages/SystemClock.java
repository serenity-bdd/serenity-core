package net.thucydides.core.pages;

import org.joda.time.DateTime;

/**
 * Find the current system time.
 */
public interface SystemClock {
    void pauseFor(long timeInMilliseconds);
    DateTime getCurrentTime();
}
