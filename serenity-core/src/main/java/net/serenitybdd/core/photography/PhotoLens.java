package net.serenitybdd.core.photography;

import java.io.IOException;

/**
 * A PhotoLens encapsulates a WebDriver or Playwright object that can be used to take screenshots
 */
public interface PhotoLens {
    byte[] takeScreenshot() throws IOException;
    default boolean canTakeScreenshot() { return true; }
}
