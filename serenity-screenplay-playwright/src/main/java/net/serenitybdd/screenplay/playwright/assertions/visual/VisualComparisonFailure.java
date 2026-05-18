package net.serenitybdd.screenplay.playwright.assertions.visual;

/**
 * Exception thrown when a visual comparison fails because the screenshot
 * differs from the baseline by more than the allowed threshold.
 *
 * @deprecated Use {@link net.serenitybdd.screenplay.visual.VisualComparisonFailure} instead.
 */
@Deprecated
public class VisualComparisonFailure extends net.serenitybdd.screenplay.visual.VisualComparisonFailure {

    public VisualComparisonFailure(String message) {
        super(message);
    }

    public VisualComparisonFailure(String message, Throwable cause) {
        super(message, cause);
    }
}
