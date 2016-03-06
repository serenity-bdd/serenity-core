package net.serenitybdd.core.time;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Stopwatch {

    private static final Logger LOGGER = LoggerFactory.getLogger(Stopwatch.class);

    long startTime = 0;

    public void start() {
        startTime = System.currentTimeMillis();
    }

    public long stop() {
        long result = System.currentTimeMillis() - startTime;
        startTime = 0;
        return result;
    }

    public long lapTime() {
        return System.currentTimeMillis() - startTime;
    }

    public long stop(String message) {
        long result = stop();
        LOGGER.debug(message + " took {0} ms", +result);
        return result;
    }
}
