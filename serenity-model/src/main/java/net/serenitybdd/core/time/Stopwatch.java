package net.serenitybdd.core.time;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;

import static java.lang.System.currentTimeMillis;

public class Stopwatch {

    private static final Logger LOGGER = LoggerFactory.getLogger(Stopwatch.class);

    long startTimeInMillis = 0;

    public static Stopwatch started() {
        Stopwatch stopwatch = new Stopwatch();
        stopwatch.start();
        return stopwatch;
    }

    public void start() {
        startTimeInMillis = currentTimeMillis();
    }

    public long stop() {
        validateStarted();
        long result = currentTimeMillis() - startTimeInMillis;
        startTimeInMillis = 0;
        return result;
    }

    private void validateStarted() {
        if (startTimeInMillis == 0) {
            throw new IllegalStateException("stopwatch is already stopped");
        }
    }

    public String lapTimeFormatted() {
        validateStarted();
        return lapTimeFormatted(currentTimeMillis() - startTimeInMillis);
    }

    public String executionTimeFormatted() {
        return lapTimeFormatted(stop());
    }

    public String lapTimeFormatted(Long executionTimeInMilliseconds) {
        return (executionTimeInMilliseconds < 1000)
                ? (executionTimeInMilliseconds + " ms") :
                (executionTimeInMilliseconds < 60000) ?
                        (new DecimalFormat("#,###.#").format(executionTimeInMilliseconds / 1000.0) + " secs") :
                        (new DecimalFormat("#,###.#").format(executionTimeInMilliseconds / 60000.0) + " mins and " +
                                new DecimalFormat("##.#").format((executionTimeInMilliseconds % 60000) / 1000.0) + " secs");
    }

    public long lapTime() {
        validateStarted();
        return currentTimeMillis() - startTimeInMillis;
    }

    public long stop(String message) {
        long result = stop();
        LOGGER.debug("{} in {}", message, lapTimeFormatted(result));
        return result;
    }
}
