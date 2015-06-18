package net.serenitybdd.core.time;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by john on 14/03/15.
 */
public class Stopwatch {

    private static final Logger LOGGER = LoggerFactory.getLogger(Stopwatch.class);

    public static Stopwatch SYSTEM = new Stopwatch();
    long counter = 0;

    public void start() {
        counter = System.currentTimeMillis();
    }

    public long stop() {
        long result = System.currentTimeMillis() - counter;
        counter = 0;
        return result;
    }

    public long stop(String message) {
        long result = stop();
        LOGGER.debug(message + " took {0} ms", +result);
        System.out.println(message + " took " + result + " ms");
        return result;
    }
}
