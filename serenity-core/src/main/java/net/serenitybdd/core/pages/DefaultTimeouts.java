package net.serenitybdd.core.pages;

import java.time.Duration;

/**
 * Created by john on 6/03/15.
 */
public class DefaultTimeouts {
    private static final Duration ZERO_SECONDS = Duration.ofSeconds(0);
    private static final Duration FIVE_SECONDS = Duration.ofSeconds(5);
    private static final Duration ONE_SECOND = Duration.ofSeconds(1);
    private static final Duration TWO_SECONDS =  Duration.ofSeconds(2);
    private static final Duration HALF_A_SECOND =  Duration.ofMillis(500);

    public static final Duration DEFAULT_WAIT_FOR_TIMEOUT = FIVE_SECONDS;
    public static final Duration DEFAULT_IMPLICIT_WAIT_TIMEOUT = TWO_SECONDS;

}
