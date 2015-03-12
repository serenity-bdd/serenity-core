package net.serenitybdd.core.pages;

import org.openqa.selenium.support.ui.Duration;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by john on 6/03/15.
 */
public class DefaultTimeouts {
    private static final Duration ZERO_SECONDS = new Duration(0, SECONDS);
    private static final Duration FIVE_SECONDS = new Duration(5, SECONDS);
    private static final Duration ONE_SECOND = new Duration(1, SECONDS);
    private static final Duration TWO_SECONDS = new Duration(2, SECONDS);
    private static final Duration HALF_A_SECOND = new Duration(2000, MILLISECONDS);

    public static final Duration DEFAULT_WAIT_FOR_TIMEOUT = FIVE_SECONDS;
    public static final Duration DEFAULT_IMPLICIT_WAIT_TIMEOUT = TWO_SECONDS;

}
