package net.serenitybdd.core.time;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;

import static java.lang.System.currentTimeMillis;

public class Stopwatch {

    private static final Logger LOGGER = LoggerFactory.getLogger(Stopwatch.class);

    long startTime = 0;

    public static Stopwatch started() {
        Stopwatch stopwatch = new Stopwatch();
        stopwatch.start();
        return stopwatch;
    }

    public void start() {
        startTime = System.currentTimeMillis();
    }

    public long stop() {
        long result = System.currentTimeMillis() - startTime;
        startTime = 0;
        return result;
    }

    public String lapTimeFormatted() {
        return lapTimeFormatted(startTime = currentTimeMillis());
    }
    public String executionTimeFormatted() {
        return lapTimeFormatted(stop());
    }

    public String lapTimeFormatted(Long executionTimeInMilliseconds) {
        return executionTimeInMilliseconds < 1000 ? executionTimeInMilliseconds + " ms" : new DecimalFormat("#,###.#").format(executionTimeInMilliseconds / 1000.0) + " secs";
    }

    public long lapTime() {
        return System.currentTimeMillis() - startTime;
    }

    public long stop(String message) {
        long result = stop();
        LOGGER.debug("{} in {}", message, lapTimeFormatted(result));
        return result;
    }
}
